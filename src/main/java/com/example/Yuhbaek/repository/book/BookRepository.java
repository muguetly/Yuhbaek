package com.example.Yuhbaek.repository.book;

import com.example.Yuhbaek.entity.book.AllBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<AllBook, Long> {

    Optional<AllBook> findByIsbn(String isbn);
}
