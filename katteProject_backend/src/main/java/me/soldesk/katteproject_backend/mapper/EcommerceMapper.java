package me.soldesk.katteproject_backend.mapper;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.ecommerce.EcommerceSettlementLogBean;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper
public interface EcommerceMapper {

    //판매 완료, 낙찰 시 주문 최초 생성
    @Insert("""
    INSERT INTO ecommerce_order (
        user_id, product_id, origin_price,auction_id,
        order_status, ordered_at
    ) VALUES (
        #{user_id}, #{product_id}, #{origin_price},#{auction_id},
        #{order_status}, #{ordered_at}
    )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "order_id")
    void insertOrder(EcommerceOrderBean order);


    @Insert("""
            INSERT INTO ecommerce_coupon (name, caution_desc, coupon_desc, sale_amount, coupon_expire_term)
            VALUES (#{name}, #{caution_desc}, #{coupon_desc}, #{sale_amount}, #{coupon_expire_term})
            """)
    void addCoupon(EcommerceCoupon coupon);

    @Insert("""
            INSERT INTO ecommerce_coupon_history (coupon_id, user_id, start_date, end_date)
            VALUES (#{coupon_id}, #{user_id}, #{start_date}, #{end_date})
            """)
    void addCouponHistory(EcommerceCouponHistory couponHistory);

    @Delete("""
            DELETE FROM ecommerce_coupon WHERE id = #{id}
            """)
    void deleteCoupon(int id);

    @Delete("""
            DELETE FROM ecommerce_coupon_history WHERE coupon_id = #{id};
            """)
    void deleteCouponHistory(int id);

    @Update("""
            UPDATE ecommerce_coupon_history SET coupon_use_date = #{coupon_use_date} WHERE user_id = #{user_id} AND coupon_id = #{coupon_id}
            """)
    void updateCouponHistory(LocalDateTime coupon_use_date, int user_id, int coupon_id);

    @Select("""
            SELECT * FROM ecommerce_coupon_history WHERE user_id = #{user_id}
            """)
    List<EcommerceCouponHistory> getAllCouponHistory(int user_id);

    @Select("""
            SELECT * FROM ecommerce_coupon WHERE id = #{coupon_id}
            """)
    EcommerceCoupon getCoupon(int coupon_id);


    @Select("""
        SELECT sale_step FROM product_check_result pcr
        JOIN product_per_sale ps ON pcr.per_sale_id = ps.id
        WHERE ps.auction_data_id = #{auction_id}
        LIMIT 1
    """)
    String getSaleStepByAuctionId(@Param("auction_id") int auction_id);

    // 정산 된 건인지 조회
    @Select("""
    SELECT is_settle_amount
    FROM auction_data
    WHERE id = #{auction_id}
    """)
    Boolean isAlreadySettled(@Param("auction_id") int auctionId);

    // 정산 대상 정보 조회 (판매자 ID, 낙찰가)
    @Select("""
    SELECT o.user_id, o.final_price
    FROM ecommerce_order o
    WHERE o.auction_id = #{auction_id}
    LIMIT 1
    """)
    @Results(id = "SettlementTargetInfo", value = {
            @Result(property = "seller_id", column = "user_id"),
            @Result(property = "final_price", column = "final_price")
    })
    EcommerceSettlementLogBean getSettlementInfo(@Param("auction_id") int auction_id);

    // 정산 완료 플래그 설정
    @Update("""
    UPDATE auction_data
    SET is_settle_amount = TRUE
    WHERE id = #{auction_id}
    """)
    void markSettlementDone(@Param("auction_id") int auction_id);

    // 정산 로그 등록
    @Insert("""
    INSERT INTO ecommerce_settlement_log (
        auction_id, seller_id, final_price, reward_amount, fee_rate, settle_time
    ) VALUES (
        #{auction_id}, #{seller_id}, #{final_price}, #{reward_amount}, #{fee_rate}, NOW()
    )
    """)
    void insertSettlementLog(EcommerceSettlementLogBean settlementLogBean);

}
