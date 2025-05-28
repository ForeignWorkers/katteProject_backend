package me.soldesk.katteproject_backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import me.soldesk.katteproject_backend.service.AuctionService;
import me.soldesk.katteproject_backend.test.ProductPriceRegisterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/product/price")
    //API Docs
    @Operation(summary = "경매 가격 등록", description = "판매 상품의 경매 시작가, 즉시 구매 여부, 기간 등을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> registerAuctionPrice(
            @RequestBody @Valid ProductPriceRegisterBean productPriceRegisterBean,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().toString());
        }

        auctionService.registerAuction(productPriceRegisterBean);
        return ResponseEntity.ok("판매 상품의 경매 가격 데이터가 등록되었습니다!");
    }
}
