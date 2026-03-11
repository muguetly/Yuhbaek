package com.example.Yuhbaek.service.Home;

import com.example.Yuhbaek.entity.Home.SearchKeyword;
import com.example.Yuhbaek.repository.Home.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchKeywordService {

    private final SearchKeywordRepository searchKeywordRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSearchKeyword(String keyword) {
        try {
            SearchKeyword searchKeyword = searchKeywordRepository.findByKeyword(keyword)
                    .orElse(SearchKeyword.builder()
                            .keyword(keyword)
                            .searchCount(0)
                            .build());

            searchKeyword.incrementSearchCount();
            searchKeywordRepository.save(searchKeyword);

            log.debug("검색어 저장/업데이트: {}, 검색 횟수: {}", keyword, searchKeyword.getSearchCount());
        } catch (Exception e) {
            log.warn("검색어 저장 실패: {}", keyword, e);
        }
    }
}