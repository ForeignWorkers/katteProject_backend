package me.soldesk.katteproject_backend.service;


import common.bean.auction.AuctionBidLog;
import common.bean.auction.AuctionDataBean;
import common.bean.auction.AuctionWinResultBean;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.product.ProductSizeWithSellPriceBean;
import common.util.*;
import me.soldesk.katteproject_backend.mapper.AuctionMapper;
import me.soldesk.katteproject_backend.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class AuctionService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AuctionMapper auctionMapper;

    @Autowired
    private EcommerceService ecommerceService;

    public void registerAuction(AuctionDataBean auctionBean) {
        //경매 시간 설정은 여기서 하지 않는다
        auctionBean.setAuction_start_time(null); // 명시적으로 null
        auctionBean.setAuction_end_time(null);   // 명시적으로 null

        //현재가 = 시작가
        auctionBean.setCurrent_price(auctionBean.getStart_price());

        //시작가 기반 수수료 계산
        auctionBean.setAuction_insert_term(
                AuctionUtil.calculateAuctionInsertTerm(auctionBean.getStart_price())
        );

        //초기 상태: 낙찰 아님
        auctionBean.setIs_settle_amount(false);

        //사이즈 정보 있을 경우, 사이즈 값 가져오기
        if (auctionBean.getProduct_size_id() != null) {
            String sizeValue = productMapper.getSizeValueById(auctionBean.getProduct_size_id());
            auctionBean.setAuction_size_value(sizeValue);
        } else {
            auctionBean.setAuction_size_value(null);
        }

        //DB에 저장
        auctionMapper.insertAuctionData(auctionBean);
    }

    // 예시: 검수 성공 → sale_step = ON_SALE 되는 시점에서 호출
    public void markAuctionStart(int auctionId, String salePeriod) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(Integer.parseInt(salePeriod));
        Date startDate = Date.from(start.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        Date endDate = Date.from(end.atZone(ZoneId.of("Asia/Seoul")).toInstant());

        auctionMapper.updateAuctionTime(auctionId, startDate, endDate);
    }

    // 경매 ID로 경매 조회
    public AuctionDataBean getAuctionById(int auctionId) {
        return auctionMapper.getAuctionById(auctionId);
    }

    // 최신 경매 id 조회
    public Integer getLatestAuctionId() {
        return auctionMapper.getLatestAuctionId();
    }

    // 입찰 로그 추가
    public void addAuctionBidLog(AuctionBidLog auctionBidLog) {
        auctionMapper.addAuctionBidLog(auctionBidLog);
    }

    // 사용자 ID 또는 입찰 ID로 입찰 로그 조회
    public List<AuctionBidLog> getAuctionBidLog(String user_id, String bid_id) {
        if (user_id != null && !user_id.trim().isEmpty()) {
            return auctionMapper.getAuctionBidLogByUserId(Integer.parseInt(user_id));
        } else {
            return auctionMapper.getAuctionBidLogByBidId(Integer.parseInt(bid_id));
        }
    }

    // 경매 ID로 입찰 로그 조회
    public List<AuctionBidLog> getAuctionBidLogByAuctionId(int auction_id) {
        return auctionMapper.getAuctionBidLogByAuctionId(auction_id);
    }

    // 입찰 로그 삭제
    public void deleteAuctionBidLog(int bid_id) {
        auctionMapper.deleteAuctionBidLog(bid_id);
    }

    public boolean isAuctionActuallyClosed(int auctionId) {
        AuctionDataBean auction = auctionMapper.getAuctionById(auctionId);
        if (auction == null) {
            throw new IllegalArgumentException("해당 auction_id는 존재하지 않습니다: " + auctionId);
        }

        boolean isTimeExpired = auction.getAuction_end_time().before(new Date());
        boolean isInstantPurchased = auction.getIs_instant_sale() && auction.getIs_settle_amount();
        boolean isWinnerSelected = auctionMapper.hasWinner(auctionId);

        return isTimeExpired || isInstantPurchased || isWinnerSelected;
    }

    //경매 거래 가능여부 판단
    public boolean isAuctionAvailableForOrder(int auctionId) {
        return !isAuctionActuallyClosed(auctionId);
    }

    // 낙찰자 조회
    public AuctionWinResultBean getAuctionWinner(int auctionId) {
        return auctionMapper.findAuctionWinner(auctionId);
    }

    // [로직] 낙찰자 기반 주문 생성
    public int generateOrderFromAuction(AuctionWinResultBean result) {
        EcommerceOrderBean order = new EcommerceOrderBean();
        order.setUser_id(result.getWinner_user_id());
        order.setAuction_id(result.getAuction_data_id());
        order.setOrigin_price(result.getResult_price());
        order.setOrder_status(EcommerceOrderBean.OrderStatus.PAYMENT_COMPLETE);
        order.setOrdered_at(new Date());

        return ecommerceService.createAuctionOrder(order);
    }

    // 즉시구매 가능한 경매 1건 조회
    public AuctionDataBean findInstantAuction(int productId, int sizeId) {
        return auctionMapper.findInstantAuction(productId, sizeId);
    }

    // 이미 주문된 경매인지 여부
    public boolean isAlreadyOrdered(int auctionId) {
        return auctionMapper.isAlreadyOrdered(auctionId);
    }

    // 경매 상태 종료 처리 (DB 직접 반영은 필요 시에만)
    public void closeAuction(int auctionId) {
        // 이 메서드는 시스템상 강제 종료가 필요한 경우만 사용
        // 현재 실제 종료 여부는 isAuctionActuallyClosed()에서 판단함
        // auctionMapper.closeAuction(auctionId); // ← 불필요한 경우 주석 처리 유지 가능
    }

    // [로직] 경매 현재가 갱신
    public void updateCurrentPrice(int auctionId, int newPrice) {
        auctionMapper.updateCurrentPrice(auctionId, newPrice);
    }

    // [로직] 경매 낙찰 상태 갱신
    public void markAuctionAsSettled(AuctionDataBean auction) {
        auctionMapper.updateAuctionSettlement(auction);
    }

    // Mapper에서 즉시 판매 최저가 조회 후 반환
    public Integer getLowestSellPrice(int productId) {
        return auctionMapper.getLowestSellPrice(productId);
    }

    // 상품 ID에 해당하는 사이즈별 최저 즉시 판매가 조회
    public List<ProductSizeWithSellPriceBean> getLowestSellPriceBySize(int productId) {
        return auctionMapper.getLowestSellPriceBySize(productId);
    }

    //경매마감시간 get.
    public Date getAuctionEndTime(int productId, String auctionSizeValue) {
        return auctionMapper.getAuctionEndTime(productId, auctionSizeValue);
    }

    public AuctionDataBean getAuctionData(int product_id, int instant_price) {
        return auctionMapper.getDataAuctionData(product_id, instant_price);
    }
}
