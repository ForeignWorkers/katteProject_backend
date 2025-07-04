package me.soldesk.katteproject_backend.mapper;

import common.bean.auction.AuctionDataBean;
import common.bean.auction.AuctionBidLog;
import common.bean.auction.AuctionWinResultBean;
import common.bean.product.ProductSizeWithSellPriceBean;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface AuctionMapper {

    // 경매 데이터 등록
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
        #{auction_start_time},
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

    //옥션 시작시간 업데이트
    @Update("""
    UPDATE auction_data
    SET auction_start_time = #{start}, auction_end_time = #{end}
    WHERE id = #{auctionId}
    """)
    void updateAuctionTime(@Param("auctionId") int auctionId,
                           @Param("start") Date start,
                           @Param("end") Date end);

    // 경매 ID로 경매 정보 조회
    @Select("SELECT * FROM auction_data WHERE id = #{auctionId}")
    AuctionDataBean getAuctionById(@Param("auctionId") int auctionId);

    //최신 경매 id 조회
    @Select("SELECT id FROM auction_data ORDER BY id DESC LIMIT 1")
    Integer getLatestAuctionId();

    // 입찰 로그 추가
    @Insert("""
        INSERT INTO auction_bid_log(
            user_id, auction_data_id, bid_take_time, bid_price, bid_limit_time
        ) VALUES (
            #{user_id}, #{auction_data_id}, NOW(), #{bid_price}, #{bid_limit_time}
        )
    """)
    void addAuctionBidLog(AuctionBidLog auctionBidLog);

    // 사용자 ID로 입찰 로그 조회
    @Select("SELECT * FROM auction_bid_log WHERE user_id = #{user_id}")
    List<AuctionBidLog> getAuctionBidLogByUserId(int userId);

    // 경매 ID로 입찰 로그 조회
    @Select("SELECT * FROM auction_bid_log WHERE auction_data_id = #{auction_id}")
    List<AuctionBidLog> getAuctionBidLogByAuctionId(int auction_id);

    // 입찰 ID로 입찰 로그 조회
    @Select("SELECT * FROM auction_bid_log WHERE id = #{bid_id}")
    List<AuctionBidLog> getAuctionBidLogByBidId(int bid_id);

    // 입찰 ID로 입찰 로그 삭제
    @Delete("DELETE FROM auction_bid_log WHERE id = #{bid_id}")
    void deleteAuctionBidLog(int bid_id);

    // 해당 경매가 이미 주문되었는지 확인
    @Select("""
        SELECT COUNT(*) > 0
        FROM ecommerce_order
        WHERE auction_id = #{auction_id}
    """)
    boolean isAlreadyOrdered(@Param("auction_id") int auction_id);

    // 낙찰자 조회 (최고가 입찰자 1명)
    @Select("""
        SELECT
            auction_data_id,
            user_id AS winner_user_id,
            bid_price AS result_price
        FROM auction_bid_log
        WHERE auction_data_id = #{auction_data_id}
        ORDER BY bid_price DESC, bid_take_time ASC
        LIMIT 1
    """)
    AuctionWinResultBean findAuctionWinner(@Param("auction_data_id") int auction_data_id);

    // 즉시구매 가능한 경매 1건 조회 (가장 먼저 등록된 순)
    @Select("""
        SELECT * FROM auction_data
        WHERE product_id = #{product_id}
          AND product_size_id = #{product_size_id}
          AND is_instant_sale = TRUE
          AND is_settle_amount = FALSE
          AND auction_end_time > NOW()
        ORDER BY auction_data_id ASC
        LIMIT 1
    """)
    AuctionDataBean findInstantAuction(
            @Param("product_id") int product_id,
            @Param("product_size_id") int product_size_id
    );

    // 경매 종료 여부 판단 (경매 종료시간 기준)
    @Select("""
        SELECT auction_end_time < NOW()
        FROM auction_data
        WHERE id = #{auction_id}
    """)
    boolean isAuctionExpired(@Param("auction_id") int auction_id);

    //거래 사이즈랑 프로덕트id로 마감기한 조회
    @Select("""
    SELECT auction_end_time
    FROM auction_data
    WHERE product_id = #{product_id}
      AND auction_size_value = #{auction_size_value}
    ORDER BY auction_end_time DESC
    LIMIT 1
""")
    Date getAuctionEndTime(
            @Param("product_id") int productId,
            @Param("auction_size_value") String auctionSizeValue
    );

    // 낙찰자 존재 여부 판단 (입찰 기록 존재 여부 기준)
    @Select("""
        SELECT COUNT(*) > 0
        FROM auction_bid_log
        WHERE auction_data_id = #{auction_id}
    """)
    boolean hasWinner(@Param("auction_id") int auction_id);

    // [쿼리] 경매 현재가 갱신
    @Update("""
    UPDATE auction_data
    SET current_price = #{current_price}
    WHERE id = #{auction_data_id}
""")
    void updateCurrentPrice(@Param("auction_data_id") int auctionId, @Param("current_price") int currentPrice);

    // [쿼리] 낙찰 처리 상태 갱신 (is_settle_amount = true)
    @Update("""
    UPDATE auction_data
    SET is_settle_amount = #{is_settle_amount}
    WHERE id = #{id}
""")
    void updateAuctionSettlement(AuctionDataBean auction);

    //경매 시간 세팅을 위한 mapper
    @Select("SELECT sale_period FROM auction_data WHERE id = #{auctionId}")
    String getSalePeriodByAuctionId(@Param("auctionId") int auctionId);

    // 해당 상품의 즉시 판매 중 최저가 조회
    @Select("""
    SELECT MIN(current_price)
    FROM auction_data
    WHERE product_id = #{product_id}            -- 특정 상품 ID에 대해
      AND is_instant_sale = TRUE                -- 즉시 판매 등록된 것 중
      AND is_settle_amount = FALSE              -- 아직 낙찰(정산)되지 않은 것만
      AND auction_end_time > NOW()              -- 유효한 경매(마감되지 않은)
""")
    Integer getLowestSellPrice(@Param("product_id") int productId);  // 결과로 최저 가격 반환

    // 사이즈별 최저 즉시 판매가 조회
    @Select("""
    SELECT auction_size_value, MIN(current_price) AS price
    FROM auction_data
    WHERE product_id = #{product_id}
      AND is_instant_sale = TRUE
      AND is_settle_amount = FALSE
      AND auction_end_time > NOW()
    GROUP BY auction_size_value
    ORDER BY auction_size_value
""")
    List<ProductSizeWithSellPriceBean> getLowestSellPriceBySize(@Param("product_id") int productId);


    @Select("""
            SELECT * FROM auction_data WHERE product_id = #{product_id} AND instant_price=#{instant_price}
            """)
    AuctionDataBean getDataAuctionData(@Param("product_id") int product_id, @Param("instant_price") int instant_price);
}
