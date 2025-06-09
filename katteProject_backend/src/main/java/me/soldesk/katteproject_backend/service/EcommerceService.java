package me.soldesk.katteproject_backend.service;

import common.bean.ecommerce.EcommerceCoupon;
import common.bean.ecommerce.EcommerceCouponHistory;
import common.bean.user.UserKatteMoneyLogBean;
import me.soldesk.katteproject_backend.mapper.EcommerceMapper;
import me.soldesk.katteproject_backend.mapper.UserMapper;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.ecommerce.EcommerceSettlementLogBean;
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
    @Autowired
    private UserMapper userMapper;

    //고정 수수료
    private static final float FEE_RATE = 0.1f;

    //주문 생성
    public int createAuctionOrder(EcommerceOrderBean request) {
        EcommerceOrderBean order = new EcommerceOrderBean();
        order.setUser_id(request.getUser_id());
        order.setProduct_id(request.getProduct_id());
        order.setOrigin_price(request.getOrigin_price());
        order.setAddress_key(request.getAddress_key());
        order.setUsed_katte_id(request.getUsed_katte_id());
        order.setAuction_id(request.getAuction_id());
        order.setOrder_status(EcommerceOrderBean.OrderStatus.PENDING); // 초기 상태
        order.setOrdered_at(new Date());

        ecommerceMapper.insertOrder(order);
        return order.getOrder_id(); // @Options로 자동 생성된 ID 반환
    }

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

    //판매금 정산
    @Transactional
    public void requestSettlement(int auctionId) {

        //판매 상태가 'sold_out'인지 확인
        String saleStep = ecommerceMapper.getSaleStepByAuctionId(auctionId);
        if (!"sold_out".equals(saleStep)) {
            throw new IllegalStateException("판매 완료 상태(sold_out)에서만 정산이 가능합니다.");
        }

        //이미 정산된 건인지 확인
        Boolean alreadySettled = ecommerceMapper.isAlreadySettled(auctionId);
        if (Boolean.TRUE.equals(alreadySettled)) {
            throw new IllegalStateException("이미 정산이 완료된 건입니다.");
        }

        //정산 대상 정보 조회 (판매자 ID, 낙찰 금액 등)
        EcommerceSettlementLogBean info = ecommerceMapper.getSettlementInfo(auctionId);
        int sellerId = info.getSeller_id();
        int totalAmount = info.getFinal_price();

        //정산 금액 계산 (예: 수수료 10%)
        int rewardAmount = (int)(totalAmount * (1 - FEE_RATE));

        // 로그 추가
        EcommerceSettlementLogBean settlementLog = new EcommerceSettlementLogBean();
        settlementLog.setAuction_id(auctionId);
        settlementLog.setSeller_id(sellerId);
        settlementLog.setFinal_price(totalAmount);
        settlementLog.setReward_amount(rewardAmount);
        settlementLog.setFee_rate(FEE_RATE);  // 수수료율은 추후 상수로 관리 가능

        ecommerceMapper.insertSettlementLog(settlementLog);

        //KatteMoney 지급 로그 등록
        UserKatteMoneyLogBean log = new UserKatteMoneyLogBean();
        log.setUser_id(sellerId);
        log.setChange_amount(rewardAmount);
        log.setReason(UserKatteMoneyLogBean.reason.CHARGE); // enum('CHARGE','USED','REFUND','REWARD') 등에서 정의된 값

        userMapper.addKatteMoneyLog(log);  // 기존에 정의된 메서드 재사용

        //정산 완료 처리
        ecommerceMapper.markSettlementDone(auctionId);  // auction_data 테이블의 is_settle_amount = true
    }
}
