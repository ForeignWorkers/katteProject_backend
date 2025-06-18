package me.soldesk.katteproject_backend.mapper;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.ecommerce.EcommerceSettlementLogBean;
import common.bean.ecommerce.*;
import common.bean.product.ProductInfoBean;
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
            DELETE FROM ecommerce_coupon_history WHERE coupon_id = #{id};
            """)
    void deleteCouponHistory(int id);

    @Delete("""
            DELETE FROM ecommerce_coupon WHERE id = #{id}
            """)
    void deleteCoupon(int id);

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
    boolean isAlreadySettled(@Param("auction_id") int auctionId);

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

    // 주문 상세 조회
    @Select("""
SELECT
    eo.id AS order_id,
    eo.user_id,
    eo.product_id,
    pi.product_name,
    eo.origin_price,
    eo.final_price,
    eo.order_status,
    DATE_FORMAT(eo.ordered_at, '%Y-%m-%d %H:%i:%s') AS ordered_at
FROM ecommerce_order eo
JOIN product_info pi ON eo.product_id = pi.product_id
WHERE eo.id = #{order_id}
""")
    EcommerceOrderDetailBean getOrderDetailById(@Param("order_id") int order_id);

    // 결제 정보 등록
    @Insert("""
INSERT INTO ecommerce_payments
 (order_id, user_id, payment_amount, pay_status, paid_at)
VALUES
 (#{order_id}, #{user_id}, #{amount}, #{status}, NOW())
""")
    @Options(useGeneratedKeys = true, keyProperty = "payment_id")
    void insertPayment(EcommercePaymentBean payment);

    // 예수금 잔액 확인
    @Select("""
SELECT katte_money FROM user_payment WHERE user_id = #{user_id}
""")
    Integer getBalanceByUserId(@Param("user_id") int userId);

    // 예수금 잔액 업데이트
    @Update("""
UPDATE user_payment SET katte_money = #{new_balance} WHERE user_id = #{user_id}
""")
    void updateBalance(@Param("user_id") int userId, @Param("new_balance") int newBalance);

    // 예수금 로그 등록
    @Insert("""
INSERT INTO user_katte_money_log (user_id, change_amount, reason, created_at)
VALUES (#{user_id}, #{change_amount}, #{reason}, NOW())
""")
    void insertMoneyLog(
            @Param("user_id") int userId,
            @Param("change_amount") int changeAmount,
            @Param("reason") String reason
    );

    // 구매확정 기록
    @Insert("""
INSERT INTO ecommerce_buy_complete (user_id, order_id, buy_complete_at, is_buy_complete)
VALUES (#{user_id}, #{order_id}, NOW(), TRUE)
""")
    void insertBuyComplete(@Param("user_id") int userId, @Param("order_id") int orderId);

    // 유저 주문 이력 조회
    @Select("""
SELECT o.id AS order_id, o.product_id, o.final_price, o.ordered_at,
       b.is_buy_complete, b.buy_complete_at
FROM ecommerce_order o
LEFT JOIN ecommerce_buy_complete b ON o.id = b.order_id
WHERE o.user_id = #{user_id}
ORDER BY o.ordered_at DESC
""")
    List<EcommerceOrderHistoryBean> getOrderHistoryByUserId(@Param("user_id") int userId);

    // 특정 경매 기준 주문 수 조회
    @Select("""
SELECT COUNT(*) FROM ecommerce_order WHERE auction_id = #{auction_data_id}
""")
    int countOrderByAuctionId(@Param("auction_data_id") int auction_data_id);

    // 주문 상태 조회
    @Select("""
SELECT order_status FROM ecommerce_order WHERE id = #{order_id} AND user_id = #{user_id}
""")
    String getOrderStatus(@Param("order_id") int order_id, @Param("user_id") int user_id);

    // 구매확정 여부
    @Select("""
SELECT EXISTS (
    SELECT 1 FROM ecommerce_buy_complete WHERE order_id = #{order_id}
)
""")
    boolean isAlreadyBuyComplete(@Param("order_id") int order_id);

    // 유저 결제 이력 조회
    @Select("""
            SELECT * FROM ecommerce_payments WHERE user_id = #{user_id} ORDER BY paid_at DESC
            """)
    List<EcommercePaymentBean> getPaymentHistoryByUserId(@Param("user_id") int user_id);

    //일정 기간 결제 내역 조회하기
    @Select("""
        SELECT
            product_id,
            final_price,
            DATE(ordered_at) AS ordered_at
        FROM ecommerce_order
        WHERE product_id = #{productId}
        AND ordered_at >= #{fromDate}
            """)
    List<EcommerceTradeLookUp> getTradesByProductAndDate(
            @Param("productId") int productId,
            @Param("fromDate") LocalDateTime fromDate
    );

    @Select("""
        SELECT
               id,
               product_id,
               product_base_id,
               model_code,
               category,
               detail_category,
               product_name,
               product_name_kor,
               product_desc,
               brand_name,
               release_date,
               release_price
        FROM
               product_info
        WHERE
            product_id = #{product_id}
    """)
    ProductInfoBean getProductInfoByProductId(int product_id);
}
