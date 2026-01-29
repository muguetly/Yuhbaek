package com.example.Yuhbaek.entity.Home;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;  // ISBN (고유 식별자)

    @Column(nullable = false, length = 500)
    private String title;  // 책 제목

    @ElementCollection
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author")
    @Builder.Default
    private List<String> authors = new ArrayList<>();  // 저자 목록

    @Column(length = 100)
    private String publisher;  // 출판사

    @Column(name = "published_date")
    private LocalDate publishedDate;  // 출판일

    @Column(length = 2000)
    private String description;  // 책 소개

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;  // 표지 이미지 URL

    private Integer price;  // 정가

    @Column(name = "sale_price")
    private Integer salePrice;  // 판매가

    @Column(length = 500)
    private String url;  // 상세 페이지 URL

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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
}
