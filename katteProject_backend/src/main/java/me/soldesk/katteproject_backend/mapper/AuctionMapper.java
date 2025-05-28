package me.soldesk.katteproject_backend.mapper;

import me.soldesk.katteproject_backend.test.ProductPriceRegisterBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface AuctionMapper {

    @Insert("""
        INSERT INTO auction_data (
            product_id,
            start_price,
            current_price,
            is_instant_sale,
            instant_price,
            sale_period,
            auction_start_time,
            auction_end_time
        ) VALUES (
            #{productId},
            #{startPrice},
            #{startPrice},                -- current_price는 start_price와 동일하게 삽입
            #{isInstantSale},
            #{instantPrice},
            #{salePeriod},
            NOW(),
            DATE_ADD(NOW(), INTERVAL ${salePeriod} DAY)
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertAuctionData(ProductPriceRegisterBean productPriceRegisterBean);
}