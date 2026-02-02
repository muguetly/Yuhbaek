package com.example.Yuhbaek.repository.catalog;

import com.example.Yuhbaek.entity.catalog.AllBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

    public interface BookSerchRepository extends JpaRepository<AllBook, Long> {

        Optional<AllBook> findByIsbn(String isbn);
    }