package me.soldesk.katteproject_backend.controller;

import common.bean.product.ProductInfoBean;
import common.bean.product.ProductSizeBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import me.soldesk.katteproject_backend.service.ProductService;
import common.bean.product.ProductCheckResultBean;
import common.bean.product.ProductPerSaleBean;
import common.bean.admin.InspectionProductViewBean;
import common.bean.admin.RegisteredProductViewBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/product")
    @Operation(summary = "상품 등록", description = "새로운 상품 정보를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> registerProduct(
            @RequestBody @Valid ProductInfoBean productBean,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().toString());
        }

        productService.registerProduct(productBean);
        return ResponseEntity.ok("상품 등록이 완료되었습니다.");
    }

    @PatchMapping("/product")
    @Operation(summary = "상품 정보 수정", description = "product_id를 쿼리로 받아 해당 상품을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> updateProduct(
            @RequestParam("product_id") int productId,
            @RequestBody ProductInfoBean productBean) {

        //product_id 설정
        productBean.setProduct_id(productId);

        productService.updateProduct(productBean);
        return ResponseEntity.ok(
                String.format("product_id=%d 상품 정보가 수정되었습니다.", productId));
    }

    @GetMapping ("/product")
    //API Docs
    @Operation(summary = "상품 조회", description = "상품을 product_id를 이용해 조회")
    public ResponseEntity<common.bean.product.ProductInfoBean> getProductById(@RequestParam("product_id") int productId) {
        ProductInfoBean product = productService.getProductById(productId);

        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/product/size")
    @Operation(summary = "상품 사이즈 등록", description = "특정 상품에 대한 사이즈 값을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> registerProductSize(
            @RequestBody @Valid ProductSizeBean sizeBean,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().toString());
        }

        productService.registerProductSize(sizeBean);
        return ResponseEntity.ok("상품 사이즈가 등록되었습니다.");
    }

    //상품 판매 등록
    @PostMapping("/product/sale")
    @Operation(summary = "판매 등록", description = "판매자가 숏폼, 상품, 경매 ID로 판매 상품을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> registerPerSale(
            @RequestBody @Valid ProductPerSaleBean bean,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().toString());
        }

        productService.registerPerSale(bean);
        return ResponseEntity.ok("판매 상품이 등록되었습니다.");
    }

    @PostMapping("/product/inspection")
    @Operation(summary = "검수 요청", description = "판매할 상품에 대해 검수를 요청합니다.")
    @ApiResponse(responseCode = "200", description = "검수 요청 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public ResponseEntity<String> requestInspection(@RequestBody ProductCheckResultBean checkResultBean) {
        productService.requestInspection(checkResultBean);
        return ResponseEntity.ok("검수 요청 완료");
    }

    @PatchMapping("/product/soldout")
    @Operation(summary = "판매 완료 처리", description = "판매 상태를 on_sale → sold_out으로 전환")
    @ApiResponse(responseCode = "200", description = "처리 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> markAsSoldOut(@RequestParam("check_result_id") int checkResultId) {
        productService.markProductAsSoldOut(checkResultId);
        return ResponseEntity.ok(String.format("check_result_id=%d → sold_out 상태로 변경됨", checkResultId));
    }

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

    @GetMapping("/products/registered")
    @Operation(summary = "등록 상품 조회", description = "등록된 상품들의 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<RegisteredProductViewBean>> getRegisteredProductList(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getRegisteredProductList(offset, size));
    }

    @GetMapping("/products/registered/count")
    @Operation(summary = "등록 상품 수 조회", description = "등록된 상품의 총 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Integer> getRegisteredProductCount() {
        return ResponseEntity.ok(productService.getRegisteredProductCount());
    }

    @GetMapping("/products/soldout")
    @Operation(summary = "판매 완료 내역 조회", description = "판매 완료된(sold_out) 항목의 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<InspectionProductViewBean>> getSoldOutList(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(productService.getSoldOutProductList(offset, size));
    }

    @GetMapping("/products/soldout/count")
    @Operation(summary = "판매 완료 항목 수 조회", description = "판매 완료 상태(sold_out)인 항목의 총 갯수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "요청 실패")
    public ResponseEntity<Integer> getSoldOutCount() {
        return ResponseEntity.ok(productService.getSoldOutProductCount());
    }
}
