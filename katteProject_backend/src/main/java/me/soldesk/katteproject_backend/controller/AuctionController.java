package me.soldesk.katteproject_backend.controller;
import common.bean.auction.AuctionBidLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import me.soldesk.katteproject_backend.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import common.bean.auction.AuctionDataBean;

import java.util.List;

@RestController
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/auction")
    //API Docs
    @Operation(summary = "옥션 데이터 등록", description = "판매 상품의 경매 시작가, 즉시 구매 여부, 기간 등을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> registerAuction(
            @RequestBody @Valid AuctionDataBean auctionBean,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().toString());
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

    @PostMapping("/auction/bid")
    //API Docs
    @Operation(summary = "경매 가격 등록", description = "판매 상품의 경매 시작가, 즉시 구매 여부, 기간 등을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> registerAuctionBid(@RequestBody AuctionBidLog auctionBidLog) {
        auctionService.addAuctionBidLog(auctionBidLog);
        return ResponseEntity.ok(String.format("%s 의 유저의 옥션 입찰이 등록 되었습니다!", auctionBidLog.getUser_id()));
    }

    @GetMapping("/auction/bid")
    //API Docs
    @Operation(summary = "경매 가격 등록", description = "판매 상품의 경매 시작가, 즉시 구매 여부, 기간 등을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<AuctionBidLog>> getAuctionBidLog(@RequestParam String user_id,
                                                                @RequestParam String bid_id) {
        return ResponseEntity.ok(auctionService.getAuctionBidLog(user_id, bid_id));
    }

    @GetMapping("/auction/bid/auction_data")
    //API Docs
    @Operation(summary = "경매 가격 등록", description = "판매 상품의 경매 시작가, 즉시 구매 여부, 기간 등을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<AuctionBidLog>> getAuctionBidLogByAuctionId(@RequestParam int auction_data_id){
        return ResponseEntity.ok(auctionService.getAuctionBidLogByAuctionId(auction_data_id));
    }

    @DeleteMapping("/auction/bid")
    //API Docs
    @Operation(summary = "경매 가격 등록", description = "판매 상품의 경매 시작가, 즉시 구매 여부, 기간 등을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> deleteAuctionBidLog(@RequestParam int bid_id){
        auctionService.deleteAuctionBidLog(bid_id);
        return ResponseEntity.ok(String.format("%d 입찰 로그가 삭제 되었습니다.", bid_id));
    }
}
