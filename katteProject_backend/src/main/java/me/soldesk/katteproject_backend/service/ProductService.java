package me.soldesk.katteproject_backend.service;

import common.bean.auction.AuctionDataBean;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.product.*;
import me.soldesk.katteproject_backend.mapper.ProductMapper;
import common.bean.admin.*;
import common.bean.product.ProductPriceSummaryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    //product 등록
    public void registerProduct(ProductInfoBean product) {
        // 브랜드 자동 등록
        if (product.getBrand_name() != null && !productMapper.isBrandExists(product.getBrand_name())) {
            productMapper.insertBrand(product.getBrand_name());
        }

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

    public ProductPriceSummaryBean getProductPriceSummary(int productId) {
        ProductPriceSummaryBean bean = new ProductPriceSummaryBean();

        // 최근 거래가
        Integer recent = productMapper.getRecentPrice(productId);
        bean.setPrice(recent);

        // 직전 거래가
        Integer prev = productMapper.getPreviousPrice(productId);
        bean.setPrevious_price(prev);

        // 직전 거래일
        Date date = productMapper.getPreviousDate(productId);
        bean.setPrevious_date(date);

        // 가격 차이
        if (recent != null && prev != null) {
            bean.setDiff_amount(recent - prev);

            // 변동률
            double percent = ((recent - prev) / (double) prev) * 100;
            bean.setDiff_percent(Math.round(percent * 10) / 10.0);
        }

        // 즉시 구매 최저가
        Integer instant = productMapper.getInstantPrice(productId);
        bean.setInstant_price(instant);

        return bean;
    }

    // 사이즈별 최저 즉시판매가 조회
    public List<ProductSizeWithPriceBean> getSizeOptionsWithPrices(int product_id) {
        return productMapper.getSizeOptionsWithPrices(product_id);
    }

    // 최근 체결 거래 내역 조회
    public List<EcommerceOrderBean> getRecentTransactionHistory(int product_id, int offset, int size) {
        return productMapper.getRecentTransactionHistory(product_id, offset, size);
    }

    // base 및 variant 상품 조회
    public List<ProductInfoBean> getRelatedBaseAndVariants(int product_base_id, int offset, int size) {
        return productMapper.getRelatedBaseAndVariants(product_base_id, offset, size);
    }

    // 해당 상품의 최저가 옥션 조회
    public AuctionDataBean getCheapestAuctionByProductId(int product_id) {
        return productMapper.getCheapestAuctionByProductId(product_id);
    }

    // 숏폼좋아요순 상품 리스트 조회
    public List<ProductKatteRecommendBean> getKatteRecommendedProducts(int offset, int size) {
        return productMapper.getKatteRecommendedProducts(offset, size);
    }

    //브랜드 매출 높은 상품 리스트 조회
    public List<ProductInfoBean> getTopProductsByBrandOrderCount(String brand_name, int offset, int size) {
        return productMapper.getTop5ProductsByBrandOrderCount(brand_name, offset, size);
    }

    // 현재 보고있는 상품과 같이 조회된 상품 리스트 조회
    public List<ProductInfoBean> getAlsoViewedProducts(int user_id, int current_product_id) {
        return productMapper.getAlsoViewedProducts(user_id, current_product_id);
    }

    // 기간별 시세 조회(기간별)
    public List<ProductPriceHistoryBean> getProductPriceHistory(int productId, String range) {
        return productMapper.getProductPriceHistory(productId, range);
    }

    // 기간별 시세 조회(전체)
    public List<ProductPriceHistoryBean> getProductPriceHistoryAll(int productId) {
        return productMapper.getProductPriceHistoryAll(productId);
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
        String category = product.getCategory(); // "shoes", "tops" 등
        if (!ALLOWED_CATEGORIES.contains(category)) {
            throw new IllegalArgumentException("해당 카테고리는 사이즈 등록이 허용되지 않습니다.");
        }

        //사이즈 값이 유효한지 검사
        if (!isValidSize(category, sizeBean.getSize_value())) {
            throw new IllegalArgumentException("해당 사이즈 값은 유효하지 않습니다.");
        }

        // 중복 사이즈 존재 여부 확인
        if (productMapper.countSize(sizeBean.getProduct_id(), sizeBean.getSize_value()) > 0) {
            throw new IllegalArgumentException("이미 등록된 사이즈입니다.");
        }

        // 유효성 통과 시 insert
        productMapper.insertProductSize(sizeBean);
    }

    public int countSize(int product_id, String size_value) {
        return productMapper.countSize(product_id, size_value);
    }

    // 상품 카테고리 중 사이즈 등록이 허용된 카테고리 목록
    private static final Set<String> ALLOWED_CATEGORIES = Set.of("신발", "상의", "아우터", "하의");

    // 카테고리별 허용 사이즈값 정의
    private boolean isValidSize(String category, String sizeValue) {
        switch (category) {
            case "신발":
                try {
                    int size = Integer.parseInt(sizeValue);
                    return size >= 220 && size <= 300 && size % 5 == 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "상의":
            case "아우터":
            case "하의":
                return List.of("XS", "S", "M", "L", "XL", "XXL", "Free").contains(sizeValue);
            default:
                return sizeValue.equalsIgnoreCase("allsize");
        }
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

    // product_per_sale의 id를 반환
    public Integer getLatestPerSaleId() {
        return productMapper.getLatestPerSaleId();
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

    public Integer getSizeId(int productId, String sizeValue) {
        return productMapper.getSizeId(productId, sizeValue);
    }

    //상품 최근 사이즈 id 조회
    public Integer getLatestSizeId() {return productMapper.selectLatestSizeId();}

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
