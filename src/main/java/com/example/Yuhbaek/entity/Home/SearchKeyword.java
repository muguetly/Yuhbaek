package com.example.Yuhbaek.entity.Home;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_keywords")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column(nullable = false)
    private Integer searchCount;

    @Column(nullable = false)
    private LocalDateTime lastSearchedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastSearchedAt = LocalDateTime.now();
        if (searchCount == null) {
            searchCount = 1;
        }
    }

    /**
     * 검색 횟수 증가
     */
    public void incrementSearchCount() {
        this.searchCount++;
        this.lastSearchedAt = LocalDateTime.now();
    }
}