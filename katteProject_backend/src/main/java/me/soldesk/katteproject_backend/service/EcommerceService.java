package me.soldesk.katteproject_backend.service;

import common.bean.ecommerce.*;
import common.bean.product.ProductInfoBean;
import me.soldesk.katteproject_backend.mapper.EcommerceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class EcommerceService {

    @Autowired
    private EcommerceMapper ecommerceMapper;

    private static final float FEE_RATE = 0.1f;

    // 주문 생성
    public int createAuctionOrder(EcommerceOrderBean request) {
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
        /*String orderStatus = ecommerceMapper.getOrderStatus(orderId, userId);
        if (!"DELIVERED".equals(orderStatus)) return false;*/

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
        if (history.getEnd_date() == null) {
            LocalDateTime sixtyDaysLater = LocalDateTime.now().plusDays(60);
            Date endDate = Date.from(sixtyDaysLater.atZone(ZoneId.systemDefault()).toInstant());
            history.setEnd_date(endDate);
        }
        ecommerceMapper.addCouponHistory(history);
    }

    // 쿠폰 사용 처리
    public void markCouponAsUsed(int userId, int couponId) {
        ecommerceMapper.updateCouponHistory(LocalDateTime.now(), userId, couponId);
    }

    // 정산 처리
    @Transactional
    public String settleOrder(int auctionId) {
        // 중복 정산 여부 확인
        Boolean alreadySettled = ecommerceMapper.isAlreadySettled(auctionId);
        if (Boolean.TRUE.equals(alreadySettled)) {
            return String.format("auction_id=%d 는 이미 정산 처리되었습니다.", auctionId);
        }

        // 정산 대상 정보 조회
        EcommerceSettlementLogBean info = ecommerceMapper.getSettlementInfo(auctionId);
        if (info == null) {
            return String.format("auction_id=%d 에 대한 정산 대상 정보가 존재하지 않습니다.", auctionId);
        }

        // 정산 금액 계산 및 세팅
        int reward = (int) (info.getFinal_price() * (1 - FEE_RATE)); // 수수료 적용
        info.setReward_amount(reward);
        info.setFee_rate(FEE_RATE);
        info.setAuction_id(auctionId);

        // 로그 기록 및 상태 변경
        ecommerceMapper.insertSettlementLog(info);
        ecommerceMapper.markSettlementDone(auctionId);

        return String.format("auction_id=%d 의 정산이 완료되었습니다. 지급 금액: %d원", auctionId, reward);
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

    public List<EcommerceTradeLookUp> getTradeLookUp(int product_id, LocalDateTime formDate) {
        return ecommerceMapper.getTradesByProductAndDate(product_id, formDate);
    }

    public ProductInfoBean getProductInfoByProductId(int product_id) {
        return ecommerceMapper.getProductInfoByProductId(product_id);
    }
}
