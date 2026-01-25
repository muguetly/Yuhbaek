package com.example.Yuhbaek.repository.Home;

import com.example.Yuhbaek.entity.Home.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR " +
            "EXISTS (SELECT 1 FROM b.authors a WHERE a LIKE %:keyword%)")
    List<Book> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT b FROM Book b WHERE b.publisher = :publisher")
    List<Book> findByPublisher(@Param("publisher") String publisher);
}
