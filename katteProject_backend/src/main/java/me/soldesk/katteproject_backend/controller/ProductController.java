package me.soldesk.katteproject_backend.controller;

import common.bean.auction.AuctionDataBean;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.product.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import me.soldesk.katteproject_backend.service.ProductService;
import common.bean.admin.*;
import common.bean.product.ProductPriceSummaryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<ProductInfoBean> getProductById(@RequestParam("product_id") int productId) {        ProductInfoBean product = productService.getProductById(productId);

        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/product/size_prices")
    @Operation(summary = "사이즈별 최저 즉시판매가 조회", description = "상품 ID에 대해 각 사이즈별 최저 즉시판매가를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    public ResponseEntity<List<ProductSizeWithPriceBean>> getSizeOptionsWithPrices(@RequestParam int product_id) {
        List<ProductSizeWithPriceBean> result = productService.getSizeOptionsWithPrices(product_id);
        return ResponseEntity.ok(result);
    }

    // 상품 가격 요약 정보 반환
    @GetMapping("/product/price_summary")
    @Operation(summary = "상품 가격 요약 정보 조회", description = "상품 ID에 대해 최근 거래가, 직전 거래가, 거래일, 즉시 구매 최저가 등을 포함한 가격 요약 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "상품 정보를 찾을 수 없음")
    public ResponseEntity<ProductPriceSummaryBean> getPriceSummary(@RequestParam("product_id") int productId) {
        ProductPriceSummaryBean result = productService.getProductPriceSummary(productId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/product/recent_transactions")
    @Operation(summary = "최근 거래 내역", description = "최근 체결된 거래 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "거래 내역이 존재하지 않음")
    public ResponseEntity<List<EcommerceOrderBean>> getRecentOrders(
            @RequestParam int product_id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size) {

        List<EcommerceOrderBean> orders = productService.getRecentTransactionHistory(product_id, offset, size);

        if (orders.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/product/cheapest_auction")
    @Operation(summary = "최저가 옥션 조회", description = "상품의 현재 최저가 옥션 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    public ResponseEntity<AuctionDataBean> getCheapestAuction(@RequestParam int product_id) {
        return ResponseEntity.ok(productService.getCheapestAuctionByProductId(product_id));
    } // >> 이 상품을 가장 싸게 팔고있는 판매자 확인하기 위함임.

    @GetMapping("/product/related")
    @Operation(summary = "베이스/파생 상품 조회", description = "base 상품과 variant 상품을 함께 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    public ResponseEntity<List<ProductInfoBean>> getRelated(
            @RequestParam int product_base_id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size) {

        List<ProductInfoBean> products = productService.getRelatedBaseAndVariants(product_base_id, offset, size);

        if (products.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/product/price_history")
    @Operation(summary = "기간별 시세 조회", description = "origin_price 기반으로 기간 내 일별 평균 시세를 반환합니다.")
    public ResponseEntity<List<ProductPriceHistoryBean>> getProductPriceHistory(
            @RequestParam("product_id") int product_id,
            @RequestParam(value = "range", defaultValue = "1 MONTH") String range) {

        // range가 "ALL"인 경우에는 전체 기간 조회
        if ("ALL".equalsIgnoreCase(range.trim())) {
            List<ProductPriceHistoryBean> allHistory = productService.getProductPriceHistoryAll(product_id);
            return ResponseEntity.ok(allHistory);
        }

        // 그 외에는 지정된 INTERVAL 범위만큼 조회
        List<ProductPriceHistoryBean> history = productService.getProductPriceHistory(product_id, range);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/product/recommend/katte_top")
    @Operation(summary = "캇테추천상품 리스트", description = "숏폼 콘텐츠 기준 좋아요 수가 높은 추천 상품 리스트를 반환합니다.")
    public ResponseEntity<List<ProductKatteRecommendBean>> getKatteRecommendedProducts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int size) {

        List<ProductKatteRecommendBean> result = productService.getKatteRecommendedProducts(offset, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/product/brand/brand_top")
    @Operation(summary = "브랜드별 매출 상위상품 리스트", description = "특정 브랜드에서 가장 많이 주문된 상품 리스트를 페이징 형식으로 조회합니다.")
    public ResponseEntity<List<ProductInfoBean>> getTopProductsByBrandOrderCount(
            @RequestParam String brand_name,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int size) {

        List<ProductInfoBean> result = productService.getTopProductsByBrandOrderCount(brand_name, offset, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/product/also_viewed")
    @Operation(summary = "같이 본 상품 리스트 조회", description = "현재 보고 있는 상품을 조회한 유저들이 함께 본 다른 상품들을 반환합니다.")
    public ResponseEntity<List<ProductInfoBean>> getAlsoViewedProducts(
            @RequestParam int user_id,
            @RequestParam int current_product_id
    ) {
        List<ProductInfoBean> result = productService.getAlsoViewedProducts(user_id, current_product_id);
        return ResponseEntity.ok(result);
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
        //중복 사이즈 예외 캐치
        try {
            productService.registerProductSize(sizeBean);
            return ResponseEntity.ok("상품 사이즈가 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); //예외 메시지 포함
        }
    }

    @GetMapping("/product/size/exists")
    public ResponseEntity<Boolean> checkSizeExists(@RequestParam int product_id, @RequestParam String size_value) {
        boolean exists = productService.countSize(product_id, size_value) > 0;
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/product/size/id")
    @Operation(summary = "사이즈 ID 조회", description = "product_id와 size_value를 기반으로 해당 사이즈의 ID를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "사이즈 정보 없음")
    public ResponseEntity<Integer> getProductSizeId(
            @RequestParam int product_id,
            @RequestParam String size_value) {

        Integer sizeId = productService.getSizeId(product_id, size_value);
        if (sizeId != null) {
            return ResponseEntity.ok(sizeId);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }


    @GetMapping("/product/size/latest")
    @Operation(summary = "최신 상품 사이즈 ID 조회", description = "가장 최근에 등록된 상품 사이즈의 ID를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "요청 실패")
    public ResponseEntity<Integer> getLatestProductSizeId() {
        Integer latestSizeId = productService.getLatestSizeId(); // 서비스에서 가장 최신 사이즈 ID 반환
        return ResponseEntity.ok(latestSizeId);
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

    @GetMapping("/product/sale/latest")
    @Operation(summary = "최신 판매 등록 ID 조회", description = "가장 마지막에 등록된 product_per_sale의 ID를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "조회 실패")
    public ResponseEntity<Integer> getLatestPerSaleId() {
        Integer latestId = productService.getLatestPerSaleId();
        return ResponseEntity.ok(latestId);
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
    public ResponseEntity<List<SoldoutProductViewBean>> getSoldOutList(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(productService.getSoldOutProductList(offset, size));
    }

    @GetMapping("/products/soldout/count")
    @Operation(summary = "판매 완료 항목 수 조회", description = "판매 완료 상태 항목의 총 갯수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<Integer> getSoldOutCount() {
        return ResponseEntity.ok(productService.getSoldOutProductCount());
    }

    @PostMapping("/product/wishlist")
    @Operation(summary = "관심상품 등록", description = "로그인된 사용자 기준으로 상품을 관심상품 리스트에 추가합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "409", description = "이미 등록된 항목")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public ResponseEntity<String> addToWishlist(@RequestBody Map<String, Integer> body, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) userId = 28; // Postman 테스트용
        Integer productId = body.get("product_id");

        if (userId == null || productId == null) {
            return ResponseEntity.badRequest().body("Missing user_id or product_id");
        }

        boolean added = productService.addProductToWishlist(userId, productId);
        if (!added) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already exists");
        }

        return ResponseEntity.ok("Added");
    }
}
