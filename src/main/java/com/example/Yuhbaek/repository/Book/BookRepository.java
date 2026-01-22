package com.example.Yuhbaek.repository.Book;

import com.example.Yuhbaek.entity.Book.AllBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<AllBook, Long> {

    Optional<AllBook> findByIsbn(String isbn);
}
