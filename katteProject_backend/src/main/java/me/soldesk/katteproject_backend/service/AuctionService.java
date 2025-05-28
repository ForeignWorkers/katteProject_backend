package me.soldesk.katteproject_backend.service;
import lombok.RequiredArgsConstructor;

import me.soldesk.katteproject_backend.mapper.AuctionMapper;
import me.soldesk.katteproject_backend.test.ProductPriceRegisterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    @Autowired
    private AuctionMapper auctionMapper;

    public void registerAuction(ProductPriceRegisterBean productPriceRegisterBean) {
        auctionMapper.insertAuctionData(productPriceRegisterBean);
    }
}
