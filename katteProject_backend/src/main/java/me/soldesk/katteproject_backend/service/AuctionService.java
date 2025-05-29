package me.soldesk.katteproject_backend.service;
import common.bean.auction.AuctionBidLog;
import common.bean.user.UserBean;
import lombok.RequiredArgsConstructor;

import me.soldesk.katteproject_backend.mapper.AuctionMapper;
import me.soldesk.katteproject_backend.test.ProductPriceRegisterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuctionService {

    @Autowired
    private AuctionMapper auctionMapper;

    public void registerAuction(ProductPriceRegisterBean productPriceRegisterBean) {
        auctionMapper.insertAuctionData(productPriceRegisterBean);
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
