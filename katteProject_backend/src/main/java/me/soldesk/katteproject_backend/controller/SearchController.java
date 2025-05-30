package me.soldesk.katteproject_backend.controller;

import common.bean.search.SearchType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.soldesk.katteproject_backend.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    @Operation(summary = "검색 기능 조회", description = "type {PRODUCT, STYLE}를 넣고, keyword 에 검색 키워드 삽입")
    @ApiResponse(responseCode = "200", description = "성공 - 각 데이터 리스트로 반환")
    @ApiResponse(responseCode = "500", description = "서버 에러")
    public ResponseEntity<List<?>> searchContent(
            @RequestParam("type") SearchType type,
            @RequestParam("keyword") String keyword) {
        List<?> result = searchService.searchByKeyword(type, keyword);
        return ResponseEntity.ok(result);
    }
}
