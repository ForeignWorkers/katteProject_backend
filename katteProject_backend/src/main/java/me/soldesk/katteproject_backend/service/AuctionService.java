package me.soldesk.katteproject_backend.service;
import common.bean.auction.AuctionBidLog;

import common.bean.auction.AuctionDataBean;
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
    private ProductMapper productMapper;  //판매 제품 사이즈 추가시 받아오기 위함

    @Autowired
    private AuctionMapper auctionMapper;

    public void registerAuction(AuctionDataBean auctionBean) {

        //판매 종료 시간 계산 (salePeriod = "3", "5", "7")
        int days = Integer.parseInt(auctionBean.getSale_period());
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(days);
        Date endDate = Date.from(endDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        auctionBean.setAuction_end_time(endDate);

        //등록 시 최초 현제 가격은 currentPrice = startPrice
        auctionBean.setCurrent_price(auctionBean.getStart_price());

        //입찰 단위 설정
        auctionBean.setAuction_insert_term(calculateAuctionInsertTerm(auctionBean.getStart_price()));

        //초기 정산 상태는 false
        auctionBean.setIs_settle_amount(false);

        // 사이즈 ID가 존재하면 size_value 조회, 아니면 null 처리
        if (auctionBean.getProduct_size_id() != null) {
            String sizeValue = productMapper.getSizeValueById(auctionBean.getProduct_size_id());
            auctionBean.setAuction_size_value(sizeValue);
        } else {
            auctionBean.setAuction_size_value(null); // 명시적 null 설정
        }

        //db insert
        auctionMapper.insertAuctionData(auctionBean);
    }

    // 가격대에 따른 최소 입찰 금액 계산
    private int calculateAuctionInsertTerm(int price) {
        if (price < 50000) return 1000; // 5만 이하 1000
        if (price < 100000) return 5000; // 10만 이하 5000
        if (price < 300000) return 10000; // 30만 이하 10000
        return 20000; // 그이상 20000
    }

    public AuctionDataBean getAuctionById(int auctionId) {
        return auctionMapper.getAuctionById(auctionId);
    }

    public void addAuctionBidLog(AuctionBidLog auctionBidLog) {
        auctionMapper.addAcutionBidLog(auctionBidLog);
    }

    public List<AuctionBidLog> getAuctionBidLog(String user_id, String bid_id) {
        if(user_id != null && !user_id.trim().isEmpty()) {
            return auctionMapper.getAuctionBidLogByUserId(Integer.parseInt(user_id));
        }else {
            return auctionMapper.getAuctionBidLogByBidId(Integer.parseInt(bid_id));
        }
    }

    public List<AuctionBidLog> getAuctionBidLogByAuctionId(int auction_id) {
        return auctionMapper.getAuctionBidLogByAuctionId(auction_id);
    }

    public void deleteAuctionBidLog(int bid_id) {
        auctionMapper.deleteAuctionLogByBidId(bid_id);
    }
}
