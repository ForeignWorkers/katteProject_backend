package me.soldesk.katteproject_backend.controller;

import common.bean.ecommerce.*;
import common.bean.product.ProductInfoBean;
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
@RequestMapping("/ecommerce")
public class EcommerceController {

    @Autowired
    private EcommerceService ecommerceService;

    @Operation(summary = "주문 생성", description = "경매 낙찰 또는 즉시구매에 따른 주문을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "주문 생성 성공")
    @ApiResponse(responseCode = "400", description = "요청 데이터 오류")
    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody EcommerceOrderBean order) {
        int orderId = ecommerceService.createAuctionOrder(order);
        return ResponseEntity.ok("주문 생성 완료 (order_id: " + orderId + ")");
    }

    @Operation(summary = "결제 실행", description = "주문에 대한 결제를 수행합니다.")
    @ApiResponse(responseCode = "200", description = "결제 성공")
    @ApiResponse(responseCode = "400", description = "예수금 부족 또는 요청 오류")
    @PostMapping("/payment/execute")
    public ResponseEntity<String> executePayment(@RequestBody EcommercePaymentBean payment) {
        int paymentId = ecommerceService.executePayment(
                payment.getOrder_id(),
                payment.getUser_id(),
                payment.getAmount()
        );
        return ResponseEntity.ok("결제 성공 (payment_id: " + paymentId + ")");
    }

    @Operation(summary = "구매 확정", description = "배송 완료된 주문에 대해 구매 확정을 수행합니다.")
    @ApiResponse(responseCode = "200", description = "구매 확정 성공")
    @ApiResponse(responseCode = "400", description = "조건 미충족 또는 잘못된 요청")
    @PatchMapping("/order/confirm")
    public ResponseEntity<String> confirmOrder(@RequestParam int order_id, @RequestParam int user_id) {
        boolean result = ecommerceService.confirmOrder(order_id, user_id);
        if (!result) return ResponseEntity.badRequest().body("구매 확정 조건 불충분");
        return ResponseEntity.ok("구매 확정 완료");
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID에 해당하는 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "주문 정보 없음")
    @GetMapping("/order/detail")
    public ResponseEntity<EcommerceOrderDetailBean> getOrderDetail(@RequestParam int order_id) {
        EcommerceOrderDetailBean detail = ecommerceService.getOrderDetail(order_id);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/order/history/detail")
    public ProductInfoBean getProductInfo(@RequestParam("product_id") int product_id) {
        return ecommerceService.getProductInfoByProductId(product_id);
    }

    @Operation(summary = "주문 이력 조회", description = "해당 유저의 전체 주문 이력을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/order/history")
    public ResponseEntity<List<EcommerceOrderHistoryBean>> getOrderHistory(@RequestParam int user_id) {
        return ResponseEntity.ok(ecommerceService.getOrderHistory(user_id));
    }

    @Operation(summary = "결제 이력 조회", description = "해당 유저의 전체 결제 이력을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/payment/history")
    public ResponseEntity<List<EcommercePaymentBean>> getPaymentHistory(@RequestParam int user_id) {
        return ResponseEntity.ok(ecommerceService.getPaymentHistoryByUserId(user_id));
    }

    @Operation(summary = "쿠폰 등록", description = "관리자가 새로운 쿠폰을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    @PostMapping("/coupon")
    public ResponseEntity<String> registerCoupon(@RequestBody EcommerceCoupon coupon) {
        ecommerceService.registerCoupon(coupon);
        return ResponseEntity.ok("쿠폰 등록 완료");
    }

    @Operation(summary = "쿠폰을 유저에게 할당", description = "특정 유저에게 쿠폰 데이터를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    @PostMapping("/coupon/assign")
    public ResponseEntity<String> assignCouponToUser(@RequestBody EcommerceCouponHistory couponHistory) {
        ecommerceService.assignCouponToUser(couponHistory);
        return ResponseEntity.ok("쿠폰 할당 완료");
    }

    @Operation(summary = "쿠폰 사용 처리", description = "유저가 사용한 쿠폰에 대해 사용일자를 기록합니다.")
    @ApiResponse(responseCode = "200", description = "사용 처리 완료")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @PatchMapping("/coupon/use")
    public ResponseEntity<String> useCoupon(@RequestParam int user_id, @RequestParam int coupon_id) {
        ecommerceService.markCouponAsUsed(user_id, coupon_id);
        return ResponseEntity.ok("쿠폰 사용 처리 완료");
    }

    @Operation(summary = "정산 처리", description = "경매 낙찰 후 판매자에게 정산 정보를 기록합니다.")
    @ApiResponse(responseCode = "200", description = "정산 성공")
    @ApiResponse(responseCode = "400", description = "정산 조건 미충족 또는 중복 정산")
    @PostMapping("/settlement")
    public ResponseEntity<String> processSettlement(@RequestParam int auction_id) {
        String result = ecommerceService.settleOrder(auction_id);

        if (result.contains("정산이 완료되었습니다")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/trade")
    public ResponseEntity<List<EcommerceTradeLookUp>> getEcommerceTrade(@RequestParam int product_id,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate) {
        List<EcommerceTradeLookUp> getList = ecommerceService.getTradeLookUp(product_id, fromDate);
        return ResponseEntity.ok(getList);
    }
}
