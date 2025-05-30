package me.soldesk.katteproject_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.soldesk.katteproject_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/product/brand/like")
    //API Docs
    @Operation(summary = "브랜드 좋아요 토글", description = "특정 브랜드에 대한 좋아요 토글")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Boolean> toggleBrandLike(@RequestParam int brand_id, @RequestParam int user_id) {
        boolean liked = productService.toggleBrandLike(brand_id, user_id);
        return ResponseEntity.ok(liked);
    }

    @PostMapping("/product/like")
    //API Docs
    @Operation(summary = "제품 좋아요 토글", description = "특정 제품에 대한 좋아요 토글")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Boolean> toggleProductLike(@RequestParam int product_id, @RequestParam int user_id) {
        boolean liked = productService.toggleProductLike(product_id, user_id);
        return ResponseEntity.ok(liked);
    }
}
