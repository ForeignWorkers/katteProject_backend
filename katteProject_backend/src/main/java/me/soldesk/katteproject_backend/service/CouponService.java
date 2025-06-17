package me.soldesk.katteproject_backend.service;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import me.soldesk.katteproject_backend.mapper.CouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {
    @Autowired
    private CouponMapper couponMapper;

    public List<EcommerceCouponHistory> getEcommerceCouponHistoryByUser_id(int user_id) {
            return couponMapper.getAllCouponsByUserId(user_id);
    }

    public EcommerceCoupon getEcommerceCouponById(int coupon_id) {
        return couponMapper.getCouponById(coupon_id);
    }
}
