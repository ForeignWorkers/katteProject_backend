package me.soldesk.katteproject_backend.mapper;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CouponMapper {
    @Select("""
            SELECT * FROM ecommerce_coupon_history WHERE user_id = #{user_id}
            """)
    List<EcommerceCouponHistory> getAllCouponsByUserId(int user_id);

    @Select("""
            SELECT * FROM ecommerce_coupon WHERE id = #{id}
            """)
    EcommerceCoupon getCouponById(int id);
}
