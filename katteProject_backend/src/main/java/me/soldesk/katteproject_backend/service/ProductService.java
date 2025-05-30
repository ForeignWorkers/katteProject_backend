package me.soldesk.katteproject_backend.service;

import me.soldesk.katteproject_backend.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    public boolean toggleBrandLike(int brand_id, int user_id) {
        if (productMapper.hasBrandLike(brand_id, user_id)) {
            productMapper.removeBrandLike(brand_id, user_id);
            productMapper.decreaseBrandLikeCount(brand_id);
            return false;  // 좋아요 취소
        } else {
            productMapper.addBrandLike(brand_id, user_id);
            productMapper.increaseBrandLikeCount(brand_id);
            return true;   // 좋아요 추가
        }
    }

    public boolean toggleProductLike(int product_id, int user_id) {
        if (productMapper.hasProductLike(product_id, user_id)) {
            productMapper.removeProductLike(product_id, user_id);
            productMapper.decreaseProductLikeCount(product_id);
            return false;  // 좋아요 취소
        } else {
            productMapper.addProductLike(product_id, user_id);
            productMapper.increaseProductLikeCount(product_id);
            return true;   // 좋아요 추가
        }
    }
}
