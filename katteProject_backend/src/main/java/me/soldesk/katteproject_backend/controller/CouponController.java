package me.soldesk.katteproject_backend.controller;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import me.soldesk.katteproject_backend.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@RestController
public class CouponController {
    @Autowired
    private CouponService couponService;

    @GetMapping("/coupon/user")
    public ResponseEntity<List<EcommerceCouponHistory>> getUserCouponsByUserId(@RequestParam("user_id") int user_id) {
        return ResponseEntity.ok(couponService.getEcommerceCouponHistoryByUser_id(user_id));
    }

    @GetMapping("/coupon/data")
    public ResponseEntity<EcommerceCoupon> getUserCouponDataById(@RequestParam("coupon_id") int coupon_id) {
        return ResponseEntity.ok(couponService.getEcommerceCouponById(coupon_id));
    }
}
