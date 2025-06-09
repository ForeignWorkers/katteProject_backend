package me.soldesk.katteproject_backend.service;

import common.bean.product.ProductInfoBean;
import common.bean.product.ProductSizeBean;
import me.soldesk.katteproject_backend.mapper.ProductMapper;
import common.bean.product.ProductCheckResultBean;
import common.bean.product.ProductPerSaleBean;
import common.bean.admin.InspectionProductViewBean;
import common.bean.admin.RegisteredProductViewBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    //product 등록
    public void registerProduct(ProductInfoBean product) {
        productMapper.insertProduct(product);
    }

    //product 정보 수정
    public void updateProduct(ProductInfoBean product) {
        productMapper.updateProduct(product);
    }

    // 상품 단건 조회
    public ProductInfoBean getProductById(int productId) {
        return productMapper.getProductById(productId);
    }

    //productSize 등록
    public void registerProductSize(ProductSizeBean sizeBean) {
        //product_id로 상품 정보 조회 가져오기
        ProductInfoBean product = productMapper.getProductById(sizeBean.getProduct_id());
        if (product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        //브랜드를 먼저 조회해서 없는 브랜드라면 자동 등록
        if (!productMapper.isBrandExists(product.getBrand_name())) {
            productMapper.insertBrand(product.getBrand_name());
        }

        //허용된 카테고리인지 검사
        String category = product.getCategory().name(); // enum to string
        if (!ALLOWED_CATEGORIES.contains(category)) {
            throw new IllegalArgumentException("해당 카테고리는 사이즈 등록이 허용되지 않습니다.");
        }

        //사이즈 값이 유효한지 검사
        if (!isValidSize(category, sizeBean.getSize_value())) {
            throw new IllegalArgumentException("해당 사이즈 값은 유효하지 않습니다.");
        }

        productMapper.insertProductSize(sizeBean);
    }

    //상품 카테고리 사이즈 허용 목록 정의(신발,옷)
    private static final Set<String> ALLOWED_CATEGORIES = Set.of("shoes", "clothing");

    //카테고리별 허용 사이즈값 정의
    private boolean isValidSize(String category, String sizeValue) {
        if (category.equals("shoes")) {
            try {
                int size = Integer.parseInt(sizeValue);
                return size >= 220 && size <= 300 && size % 5 == 0;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (category.equals("clothing")) {
            return List.of("XS", "S", "M", "L", "XL", "XXL", "Free").contains(sizeValue);
        }
        // 기타 카테고리는 사이즈 등록 불가
        return false;
    }

    //판매 상품 등록
    public void registerPerSale(ProductPerSaleBean perSaleBean) {
        productMapper.insertPerSale(perSaleBean);
    }

    //검수 요청
    @Transactional
    public void requestInspection(ProductCheckResultBean checkResultBean) {
        checkResultBean.setCheck_step(ProductCheckResultBean.CheckStep.IN_PROGRESS);
        checkResultBean.setSale_step(ProductCheckResultBean.SaleStep.INSPECTION);

        productMapper.insertCheckResult(checkResultBean);
    }

    //판매 완료 요청
    public void markProductAsSoldOut(int checkResultId) {
        productMapper.markAsSoldOut(checkResultId);
    }

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

    //등록 상품 리스트 조회
    public List<RegisteredProductViewBean> getRegisteredProductList(int offset, int size) {
        return productMapper.getRegisteredProductList(offset, size);
    }

    //등록상품 수 조회
    public int getRegisteredProductCount() {
        return productMapper.getRegisteredProductCount();
    }

    //판매 완료 내역 리스트 조회
    public List<InspectionProductViewBean> getSoldOutProductList(int offset, int size) {
        return productMapper.getSoldOutList(offset, size);
    }

    //전체 판매 완료 수 조회
    public int getSoldOutProductCount() {
        return productMapper.getSoldOutCount();
    }

}
