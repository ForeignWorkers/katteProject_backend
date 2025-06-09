package me.soldesk.katteproject_backend.mapper;

import common.bean.auction.AuctionDataBean;
import common.bean.auction.AuctionBidLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AuctionMapper {

    @Insert("""
    INSERT INTO auction_data (
        product_id,
        product_size_id,
        auction_size_value,
        auction_start_time,
        sale_period,
        auction_end_time,
        auction_insert_term,
        start_price,
        current_price,
        instant_price,
        is_instant_sale,
        is_settle_amount
    ) VALUES (
        #{product_id},
        #{product_size_id},
        #{auction_size_value},
        NOW(),
        #{sale_period},
        #{auction_end_time},
        #{auction_insert_term},
        #{start_price},
        #{current_price},
        #{instant_price},
        #{is_instant_sale},
        #{is_settle_amount}
    )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertAuctionData(AuctionDataBean auctionBean);


    @Select("""
    SELECT * FROM auction_data WHERE id = #{auctionId}
    """)
    AuctionDataBean getAuctionById(@Param("auctionId") int auctionId);

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