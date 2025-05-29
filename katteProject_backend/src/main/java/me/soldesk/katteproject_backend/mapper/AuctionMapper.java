package me.soldesk.katteproject_backend.mapper;

import common.bean.auction.AuctionBidLog;
import me.soldesk.katteproject_backend.test.ProductPriceRegisterBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

    @Insert("INSERT INTO auction_bid_log(user_id, auction_data_id, bid_take_time, bid_price, bid_limit_time)\n" +
            "VALUES (#{user_id},#{auction_data_id},NOW(),#{bid_price},#{bid_limit_time});")
    void addAcutionBidLog(AuctionBidLog auctionBidLog);

    @Select("SELECT * FROM auction_bid_log WHERE user_id = #{user_id}")
    List<AuctionBidLog> getAuctionBidLogByUserId(int userId);

    @Select("SELECT * FROM auction_bid_log WHERE auction_data_id = #{auction_id}")
    List<AuctionBidLog> getAuctionBidLogByAuctionId(int auction_id);

    @Select("SELECT * FROM auction_bid_log WHERE id = #{bid_id}")
    List<AuctionBidLog>  getAuctionBidLogByBidId(int bid_id);

    @Delete("DELETE FROM auction_bid_log WHERE id = #{bid_id}")
    void deleteAuctionLogByBidId(int bid_id);
}