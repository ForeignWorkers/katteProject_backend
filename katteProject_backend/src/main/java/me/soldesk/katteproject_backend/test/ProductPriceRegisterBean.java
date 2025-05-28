package me.soldesk.katteproject_backend.test;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductPriceRegisterBean {
    @Schema(description = "경매 시작가", example = "100000", required = true)
    private int startPrice;

    @Schema(description = "즉시 구매 여부", example = "true", required = true)
    private boolean isInstantSale;

    @Schema(description = "즉시 구매가 (선택사항, isInstantSale = true일 때만 사용)", example = "150000", required = false)
    private Integer instantPrice;

    @Schema(description = "판매 기간 일수 (3 / 5 / 7)", example = "5", required = true)
    private String salePeriod;
}
