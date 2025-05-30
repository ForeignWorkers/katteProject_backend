package me.soldesk.katteproject_backend.mapper;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper
public interface EcommerceMapper {
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
}
