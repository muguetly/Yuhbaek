package com.example.Yuhbaek.entity.Book;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "book",
        indexes = { @Index(name = "idx_book_isbn", columnList = "isbn", unique = true) })
@Getter @Setter
public class AllBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String authorText;  // authors를 간단히 문자열로 저장(필요하면 나중에 분리)
    private String publisher;
    private String coverUrl;
    private String genre;
}
