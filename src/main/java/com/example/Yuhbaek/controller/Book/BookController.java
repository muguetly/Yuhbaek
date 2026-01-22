package com.example.Yuhbaek.controller.Book;

import com.example.Yuhbaek.dto.Book.BookSaveRequest;
import com.example.Yuhbaek.dto.Book.BookSearchResponse;
import com.example.Yuhbaek.service.Book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Tag(name = "책 API", description = "책 검색 및 책 정보 저장 관련 API")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 책 검색
     */
    @Operation(summary = "책 검색", description = "카카오 책 API를 이용해 책을 검색합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<BookSearchResponse> search(
            @Parameter(description = "검색어 (책 제목/저자/출판사)", example = "해리포터")
            @RequestParam @NotBlank String query,

            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "한 페이지당 조회 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(bookService.search(query, page, size));
    }

    /**
     * 책 저장
     */
    @Operation(summary = "책 저장", description = "선택한 책을 DB에 저장합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<Long> save(
            @Parameter(description = "저장할 책 정보", required = true)
            @RequestBody @Valid BookSaveRequest request
    ) {
        Long id = bookService.saveOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}
