package me.soldesk.katteproject_backend.service;

import common.bean.ecommerce.*;
import me.soldesk.katteproject_backend.mapper.EcommerceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class EcommerceService {

    @Autowired
    private EcommerceMapper ecommerceMapper;

    private static final float FEE_RATE = 0.1f;

    // 주문 생성
    public int createAuctionOrder(EcommerceOrderBean request) {
        request.setOrdered_at(new Date());
        request.setOrder_status(EcommerceOrderBean.OrderStatus.PAYMENT_COMPLETE);
        ecommerceMapper.insertOrder(request);
        return request.getOrder_id();
    }

    // 결제 실행
    @Transactional
    public int executePayment(int orderId, int userId, int amount) {
        Integer balance = ecommerceMapper.getBalanceByUserId(userId);
        if (balance == null || balance < amount) {
            throw new IllegalStateException("예수금 부족");
        }

        // 예수금 차감
        ecommerceMapper.updateBalance(userId, balance - amount);

        // 결제 로그 등록
        EcommercePaymentBean payment = new EcommercePaymentBean();
        payment.setOrder_id(orderId);
        payment.setUser_id(userId);
        payment.setAmount(amount);
        payment.setStatus(EcommercePaymentBean.PaymentStatus.PAID);
        ecommerceMapper.insertPayment(payment);

        // 예수금 로그 등록
        ecommerceMapper.insertMoneyLog(userId, -amount, "order_use");

        return payment.getPayment_id();
    }

    // 구매확정
    @Transactional
    public boolean confirmOrder(int orderId, int userId) {
        String orderStatus = ecommerceMapper.getOrderStatus(orderId, userId);
        if (!"delivered".equals(orderStatus)) return false;

        boolean alreadyConfirmed = ecommerceMapper.isAlreadyBuyComplete(orderId);
        if (alreadyConfirmed) return false;

        ecommerceMapper.insertBuyComplete(userId, orderId);
        return true;
    }

    // 쿠폰 등록
    public void registerCoupon(EcommerceCoupon coupon) {
        ecommerceMapper.addCoupon(coupon);
    }

    // 쿠폰 이력 추가
    public void assignCouponToUser(EcommerceCouponHistory history) {
        ecommerceMapper.addCouponHistory(history);
    }

    // 쿠폰 사용 처리
    public void markCouponAsUsed(int userId, int couponId) {
        ecommerceMapper.updateCouponHistory(LocalDateTime.now(), userId, couponId);
    }

    // 정산 처리
    @Transactional
    public void settleOrder(int auctionId) {
        Boolean alreadySettled = ecommerceMapper.isAlreadySettled(auctionId);
        if (Boolean.TRUE.equals(alreadySettled)) return;

        EcommerceSettlementLogBean info = ecommerceMapper.getSettlementInfo(auctionId);
        if (info == null) return;

        int reward = (int) (info.getFinal_price() * (1 - FEE_RATE));
        info.setReward_amount(reward);
        info.setFee_rate(FEE_RATE);
        info.setAuction_id(auctionId);

        ecommerceMapper.insertSettlementLog(info);
        ecommerceMapper.markSettlementDone(auctionId);
    }

    // 주문 상세 조회
    public EcommerceOrderDetailBean getOrderDetail(int orderId) {
        return ecommerceMapper.getOrderDetailById(orderId);
    }

    // 결제 이력 조회
    public List<EcommercePaymentBean> getPaymentHistoryByUserId(int userId) {
        return ecommerceMapper.getPaymentHistoryByUserId(userId);
    }

    // 주문 이력 조회
    public List<EcommerceOrderHistoryBean> getOrderHistory(int userId) {
        return ecommerceMapper.getOrderHistoryByUserId(userId);
    }
}
