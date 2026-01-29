package com.example.Yuhbaek.repository.Home;

import com.example.Yuhbaek.entity.Home.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {

    Optional<SearchKeyword> findByKeyword(String keyword);

    /**
     * 인기 검색어 (검색 횟수 많은 순)
     */
    @Query("SELECT s FROM SearchKeyword s ORDER BY s.searchCount DESC")
    List<SearchKeyword> findTopBySearchCount(org.springframework.data.domain.Pageable pageable);

    /**
     * 최신 검색어 (최근 검색 순)
     */
    @Query("SELECT s FROM SearchKeyword s ORDER BY s.lastSearchedAt DESC")
    List<SearchKeyword> findTopByLastSearchedAt(org.springframework.data.domain.Pageable pageable);
}