package me.soldesk.katteproject_backend.controller;

import common.bean.auction.AuctionBidLog;
import common.bean.auction.AuctionDataBean;
import common.bean.auction.AuctionWinResultBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import me.soldesk.katteproject_backend.service.AuctionService;

import common.bean.product.ProductSizeWithSellPriceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/auction")
    @Operation(summary = "옥션 데이터 등록", description = "판매 상품의 경매 시작가, 즉시 구매 여부, 기간 등을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> registerAuction(
            @RequestBody @Valid AuctionDataBean auctionBean,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().toString());
        }

        // product_id가 0이면 등록 중단
        if (auctionBean.getProduct_id() == 0) {
            throw new IllegalArgumentException("product_id는 필수입니다.");
        }

        auctionService.registerAuction(auctionBean);
        return ResponseEntity.ok("상품의 옥션 데이터가 등록되었습니다!");
    }

    @GetMapping("/auction")
    @Operation(summary = "옥션 조회", description = "auction_id를 기준으로 경매 데이터를 조회합니다.")
    public ResponseEntity<AuctionDataBean> getAuctionById(@RequestParam("auction_id") int auctionId) {
        AuctionDataBean auction = auctionService.getAuctionById(auctionId);
        if (auction != null) {
            return ResponseEntity.ok(auction);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/auction/latest")
    @Operation(summary = "최근 경매 ID 조회")
    public ResponseEntity<Integer> getLatestAuctionId() {
        return ResponseEntity.ok(auctionService.getLatestAuctionId());
    }

    @PostMapping("/auction/bid")
    @Operation(summary = "경매 입찰 등록", description = "특정 경매에 대해 사용자의 입찰 가격을 등록합니다. 거래 종료 시 입찰 불가합니다.")
    @ApiResponse(responseCode = "200", description = "입찰 등록 성공")
    @ApiResponse(responseCode = "400", description = "입찰 실패")
    public ResponseEntity<String> registerAuctionBid(@RequestBody AuctionBidLog auctionBidLog) {
        int auctionId = auctionBidLog.getAuction_data_id();
        int bidPrice = auctionBidLog.getBid_price();
        int userId = auctionBidLog.getUser_id();

        // 1. 거래 가능 여부 확인
        if (!auctionService.isAuctionAvailableForOrder(auctionId)) {
            return ResponseEntity.badRequest().body("해당 경매는 이미 거래 완료되었거나 종료된 상태입니다.");
        }

        // 2. 경매 정보 조회
        AuctionDataBean auction = auctionService.getAuctionById(auctionId);
        if (bidPrice <= auction.getCurrent_price()) {
            return ResponseEntity.badRequest().body("입찰가는 현재가보다 높아야 합니다.");
        }

        // 3. 입찰 로그 저장
        auctionService.addAuctionBidLog(auctionBidLog);

        // 4. 현재가 갱신
        auctionService.updateCurrentPrice(auctionId, bidPrice);

        // 5. 즉시가 도달 시 낙찰 처리 + 주문 생성
        if (auction.getIs_instant_sale() && bidPrice >= auction.getInstant_price()) {
            auction.setIs_settle_amount(true);
            auctionService.markAuctionAsSettled(auction);

            AuctionWinResultBean result = new AuctionWinResultBean();
            result.setAuction_data_id(auctionId);
            result.setWinner_user_id(userId);
            result.setResult_price(bidPrice);

            int orderId = auctionService.generateOrderFromAuction(result);
            return ResponseEntity.ok("즉시구매가 도달하여 주문이 자동 생성되었습니다! order_id: " + orderId);
        }

        return ResponseEntity.ok(String.format("%d번 유저의 입찰이 등록되었습니다!", userId));
    }

    @GetMapping("/auction/bid")
    @Operation(summary = "입찰 로그 조회", description = "사용자 ID 또는 입찰 ID로 경매 입찰 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<List<AuctionBidLog>> getAuctionBidLog(
            @RequestParam String user_id,
            @RequestParam String bid_id) {
        return ResponseEntity.ok(auctionService.getAuctionBidLog(user_id, bid_id));
    }

    @GetMapping("/auction/bid/auction_data")
    @Operation(summary = "경매별 입찰 로그 조회", description = "경매 ID로 모든 입찰 기록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<List<AuctionBidLog>> getAuctionBidLogByAuctionId(@RequestParam int auction_data_id) {
        return ResponseEntity.ok(auctionService.getAuctionBidLogByAuctionId(auction_data_id));
    }

    @DeleteMapping("/auction/bid")
    @Operation(summary = "입찰 로그 삭제", description = "입찰 ID를 기준으로 입찰 로그를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    public ResponseEntity<String> deleteAuctionBidLog(@RequestParam int bid_id) {
        auctionService.deleteAuctionBidLog(bid_id);
        return ResponseEntity.ok(String.format("%d 입찰 로그가 삭제 되었습니다.", bid_id));
    }

    @PostMapping("/auction/confirm-win")
    @Operation(summary = "낙찰 확정 및 주문 생성", description = "경매가 종료되고 낙찰자가 존재할 경우, 주문을 생성합니다.")
    public ResponseEntity<?> confirmAuctionWin(@RequestBody AuctionWinResultBean request) {
        int auctionId = request.getAuction_data_id();

        if (auctionService.isAuctionAvailableForOrder(auctionId)) {
            return ResponseEntity.badRequest().body("아직 경매가 종료되지 않았습니다.");
        }

        AuctionWinResultBean result = auctionService.getAuctionWinner(auctionId);
        if (result == null) {
            return ResponseEntity.badRequest().body("낙찰자가 없습니다.");
        }

        int orderId = auctionService.generateOrderFromAuction(result);
        return ResponseEntity.ok(Map.of("order_id", orderId));
    }

    // [GET] /auction/lowest-sell-price?product_id=xxx 형태로 요청 시 응답
    @GetMapping("auction/lowest_sell_price")
    public ResponseEntity<Integer> getLowestSellPrice(@RequestParam int product_id) {
        return ResponseEntity.ok(auctionService.getLowestSellPrice(product_id)); // 응답 본문에 최저가 포함
    }

    // 사이즈별 최저 즉시 판매가를 반환하는 API
    @GetMapping("/auction/lowest_sell_price/size")
    public ResponseEntity<List<ProductSizeWithSellPriceBean>> getLowestSellPriceBySize(
            @RequestParam("product_id") int productId
    ) {
        return ResponseEntity.ok(auctionService.getLowestSellPriceBySize(productId));
    }

    // 🟡 입찰 마감일 조회 API
    @GetMapping("auction/end_time")
    public ResponseEntity<String> getAuctionEndTime(
            @RequestParam("product_id") int productId,
            @RequestParam("size") String sizeValue) {

        Date endTime = auctionService.getAuctionEndTime(productId, sizeValue);

        if (endTime == null) {
            return ResponseEntity.ok("마감 정보 없음");
        }

        // ✅ Date → String 변환 (ex: 2025-06-20 23:59)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String endTimeStr = sdf.format(endTime);

        return ResponseEntity.ok(endTimeStr);
    }


    @GetMapping("auction/getData")
    public ResponseEntity<AuctionDataBean> getAuctionData(@RequestParam("product_id") int product_id,
                                                          @RequestParam("instant_price") int instant_price){
        return ResponseEntity.ok(auctionService.getAuctionData(product_id, instant_price));
    }
}
