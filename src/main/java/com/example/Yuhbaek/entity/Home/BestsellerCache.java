package com.example.Yuhbaek.entity.Home;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 베스트셀러 DB 저장용 엔티티
 * - 한번 조회한 베스트셀러 데이터를 DB에 저장
 * - 서버 재시작해도 데이터 유지
 * - API 호출 최소화
 */
@Entity
@Table(name = "bestseller_cache")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestsellerCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 캐시 키 (예: "Book_Bestseller_10")
     * searchTarget_queryType_maxResults 조합
     */
    @Column(nullable = false, unique = true, length = 100)
    private String cacheKey;

    /**
     * 베스트셀러 데이터 (JSON 형태로 저장)
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String data;

    /**
     * 데이터 생성 시각
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 데이터 수정 시각
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 데이터가 최신인지 확인 (24시간 이내)
     * 테스트용으로 24시간으로 설정
     */
    public boolean isRecent() {
        return updatedAt.isAfter(LocalDateTime.now().minusHours(24));
    }
}