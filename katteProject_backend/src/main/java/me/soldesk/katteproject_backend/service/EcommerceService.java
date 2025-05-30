package me.soldesk.katteproject_backend.service;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import me.soldesk.katteproject_backend.mapper.EcommerceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class EcommerceService {
    @Autowired
    private EcommerceMapper ecommerceMapper;

    public void addCouponData(EcommerceCoupon coupon) {
        ecommerceMapper.addCoupon(coupon);
    }

    public void deleteCouponData(int coupon_id) {
        ecommerceMapper.deleteCouponHistory(coupon_id);
        ecommerceMapper.deleteCoupon(coupon_id);
    }

    public void addCouponHistoryUser(EcommerceCouponHistory couponHistory) {
        ecommerceMapper.addCouponHistory(couponHistory);
    }

    public void updateCouponHistoryUse(LocalDateTime coupon_use_date, int user_id, int coupon_id) {
        ecommerceMapper.updateCouponHistory(coupon_use_date, user_id, coupon_id);
    }

    public List<EcommerceCouponHistory> getCouponHistoryByUserId(int user_id) {
        return ecommerceMapper.getAllCouponHistory(user_id);
    }

    public EcommerceCoupon getCouponDataByCouponId(int coupon_id) {
        return ecommerceMapper.getCoupon(coupon_id);
    }
}
