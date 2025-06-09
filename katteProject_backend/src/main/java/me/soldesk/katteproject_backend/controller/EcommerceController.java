package me.soldesk.katteproject_backend.controller;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.soldesk.katteproject_backend.service.EcommerceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class EcommerceController {
    @Autowired
    private EcommerceService ecommerceService;

    @PostMapping("/admin/coupon")
    //API Docs
    @Operation(summary = "쿠폰데이터 등록", description = "쿠폰 데이터를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> registerCouponData(@RequestBody EcommerceCoupon coupon) {
        ecommerceService.addCouponData(coupon);
        return ResponseEntity.ok("쿠폰 등록이 완료 되었습니다.");
    }

    @DeleteMapping("/admin/coupon")
    //API Docs
    @Operation(summary = "쿠폰데이터 삭제", description = "쿠폰 데이터를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> deleteCouponData(@RequestParam int coupon_id) {
        ecommerceService.deleteCouponData(coupon_id);
        return ResponseEntity.ok("쿠폰 삭제가 완료 되었습니다.");
    }

    @PostMapping("/coupon")
    //API Docs
    @Operation(summary = "쿠폰를 유저에게 할당", description = "특정 유저에게 쿠폰 데이터를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> addCouponForUser(@RequestBody EcommerceCouponHistory couponHistory) {
        ecommerceService.addCouponHistoryUser(couponHistory);
        return ResponseEntity.ok(String.format("유저 %d 쿠폰 등록이 완료 되었습니다",couponHistory.getUser_id()));
    }

    @PatchMapping("/coupon")
    public ResponseEntity<String> updateCouponHistory(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime use_date,
            @RequestParam int coupon_id,
            @RequestParam int user_id) {

        ecommerceService.updateCouponHistoryUse(use_date, user_id, coupon_id);
        return ResponseEntity.ok(String.format("유저 %d 쿠폰이 사용 완료 되었습니다", user_id));
    }

    @GetMapping("/coupon/user")
    //API Docs
    @Operation(summary = "유저의 쿠폰을 조회", description = "")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<EcommerceCouponHistory>> getCouponHistory(@RequestParam int user_id) {
        return ResponseEntity.ok(ecommerceService.getCouponHistoryByUserId(user_id));
    }

    @GetMapping("/coupon/data")
    //API Docs
    @Operation(summary = "쿠폰의 데이터 조회", description = "")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<EcommerceCoupon> getCouponData(@RequestParam int coupon_id) {
        return ResponseEntity.ok(ecommerceService.getCouponDataByCouponId(coupon_id));
    }

    @PatchMapping("/settlement")
    @Operation(summary = "판매금 정산 처리 요청", description = "auction_id로 낙찰 정산 상태를 처리합니다. 상태가 'sold_out'일 경우만 정산이 가능합니다.")
    @ApiResponse(responseCode = "200", description = "정산 처리 성공")
    @ApiResponse(responseCode = "400", description = "정산 처리 실패 또는 조건 불충족")
    public ResponseEntity<String> requestSettlement(@RequestParam("auction_id") int auctionId) {
        ecommerceService.requestSettlement(auctionId);
        return ResponseEntity.ok(String.format("auction_id=%d의 낙찰 금액 정산 처리가 완료되었습니다.", auctionId));
    }
}