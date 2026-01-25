package com.example.Yuhbaek.repository.Home;

import com.example.Yuhbaek.entity.Home.BestsellerCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BestsellerCacheRepository extends JpaRepository<BestsellerCache, Long> {

    /**
     * 캐시 키로 데이터 조회
     */
    Optional<BestsellerCache> findByCacheKey(String cacheKey);

    /**
     * 캐시 키 존재 여부 확인
     */
    boolean existsByCacheKey(String cacheKey);
}