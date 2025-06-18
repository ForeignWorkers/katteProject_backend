package me.soldesk.katteproject_backend.mapper;

import common.bean.auction.AuctionDataBean;
import common.bean.ecommerce.EcommerceOrderBean;
import common.bean.product.*;
import common.bean.admin.*;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ProductMapper {

    // 상품 정보 등록
    @Insert("""
    INSERT INTO product_info (
        product_id,
        product_base_id,
        model_code,
        category,
        detail_category,
        product_name,
        product_name_kor,
        product_desc,
        brand_name,
        release_date,
        release_price
    ) VALUES (
        #{product_id},
        #{product_base_id},
        #{model_code},
        #{category},
        #{detail_category},
        #{product_name},
        #{product_name_kor},
        #{product_desc},
        #{brand_name},
        #{release_date},
        #{release_price}
    )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertProduct(ProductInfoBean product);

    // 상품 정보 수정
    @Update("""
    <script>
    UPDATE product_info
    <set>
        <if test="product_base_id != null">product_base_id = #{product_base_id},</if>
        <if test="model_code != null">model_code = #{model_code},</if>
        <if test="category != null">category = #{category},</if>
        <if test="detail_category != null">detail_category = #{detail_category},</if>
        <if test="product_name != null">product_name = #{product_name},</if>
        <if test="product_name_kor != null">product_name_kor = #{product_name_kor},</if>
        <if test="product_desc != null">product_desc = #{product_desc},</if>
        <if test="brand_name != null">brand_name = #{brand_name},</if>
        <if test="release_date != null">release_date = #{release_date},</if>
        <if test="release_price != null">release_price = #{release_price},</if>
    </set>
    WHERE product_id = #{product_id}
    </script>
    """)
    void updateProduct(ProductInfoBean product);

    //제품 사이즈 등록
    @Insert("""
    INSERT INTO product_size (
        product_id,
        size_value
    ) VALUES (
        #{product_id},
        #{size_value}
    )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertProductSize(ProductSizeBean sizeBean);

    //제품 사이즈 중복 검사 처리 위한 쿼리
    @Select("""
    SELECT COUNT(*) FROM product_size
    WHERE product_id = #{product_id} AND size_value = #{size_value}
    """)
    int countSize(@Param("product_id") int product_id, @Param("size_value") String size_value);

    // 단일 상품 조회: 상품 ID를 기준으로 product_info 테이블에서 상품 정보
    @Select("SELECT * FROM product_info WHERE product_id = #{product_id}")
    ProductInfoBean getProductById(@Param("product_id") int product_id);

    // 최근 거래가 (가장 최신 주문 1건의 origin_price)
    @Select("SELECT origin_price FROM ecommerce_order WHERE product_id = #{product_id} ORDER BY ordered_at DESC LIMIT 1")
    Integer getRecentPrice(int product_id);

    // 직전 거래가 (두 번째 최신 주문의 origin_price)
    @Select("SELECT origin_price FROM ecommerce_order WHERE product_id = #{product_id} ORDER BY ordered_at DESC LIMIT 1 OFFSET 1")
    Integer getPreviousPrice(int product_id);

    // 직전 거래일 (두 번째 최신 주문의 ordered_at)
    @Select("SELECT ordered_at FROM ecommerce_order WHERE product_id = #{product_id} ORDER BY ordered_at DESC LIMIT 1 OFFSET 1")
    Date getPreviousDate(int product_id);

    // 즉시 구매 최저가 (판매중인 auction_data 기준)
    @Select("SELECT MIN(instant_price) FROM auction_data WHERE product_id = #{product_id}")
    Integer getInstantPrice(int product_id);

    // 상품 사이즈별 최저가 조회
    @Select("""
    SELECT
    ps.size_value AS auction_size_value,
    MIN(ad.instant_price) AS price
    FROM
    product_size ps
    LEFT JOIN
    auction_data ad
    ON
    ps.id = ad.product_size_id
    AND ad.is_instant_sale = true
    WHERE
    ps.product_id = #{product_id}
    GROUP BY
    ps.size_value
    ORDER BY
    ps.size_value
""")
    List<ProductSizeWithPriceBean> getSizeOptionsWithPrices(@Param("product_id") int product_id);

    // 최근 체결된 거래(오더) n(ex. 10)건 조회
    @Select("SELECT * FROM ecommerce_order WHERE product_id = #{product_id} ORDER BY ordered_at DESC " +
            "LIMIT #{size} OFFSET #{offset}")
    List<EcommerceOrderBean> getRecentTransactionHistory(
            @Param("product_id") int product_id,
            @Param("offset") int offset,
            @Param("size") int size);

    // 해당 상품의 즉시가 중 가장 싼 가격(최저가) 1건 조회
    @Select("""
        SELECT * FROM auction_data
        WHERE product_id = #{product_id}
        ORDER BY instant_price ASC
        LIMIT 1
    """)
    AuctionDataBean getCheapestAuctionByProductId(@Param("product_id") int product_id);

    // base 상품 및 그 파생 상품 조회
    @Select("SELECT * FROM product_info WHERE product_base_id = #{product_base_id} OR product_id = #{product_base_id} " +
            "LIMIT #{size} OFFSET #{offset}")
    List<ProductInfoBean> getRelatedBaseAndVariants(
            @Param("product_base_id") int product_base_id,
            @Param("offset") int offset,
            @Param("size") int size);

    // 기간별 조회 (1개월, 3개월, 6개월, 1년 등)
    @Select("""
        SELECT 
            DATE(ordered_at) AS date,
            ROUND(AVG(origin_price)) AS price
        FROM 
            ecommerce_order
        WHERE 
            product_id = #{product_id}
            AND ordered_at >= DATE_SUB(CURDATE(), INTERVAL ${range})
        GROUP BY 
            DATE(ordered_at)
        ORDER BY 
            DATE(ordered_at) ASC
        """)
    List<ProductPriceHistoryBean> getProductPriceHistory(
            @Param("product_id") int productId,
            @Param("range") String range
    );

    // [신규] 전체 기간 조회용 쿼리
    @Select("""
        SELECT 
            DATE(ordered_at) AS date,
            ROUND(AVG(origin_price)) AS price
        FROM 
            ecommerce_order
        WHERE 
            product_id = #{product_id}
        GROUP BY 
            DATE(ordered_at)
        ORDER BY 
            DATE(ordered_at) ASC
        """)
    List<ProductPriceHistoryBean> getProductPriceHistoryAll(
            @Param("product_id") int productId
    );

    // 숏폼 좋아요높은순 리스트
    @Select("""
    SELECT 
        pi.product_id,
        pi.product_name,
        pi.brand_name,
        cs.content_url AS shortform_content_url,
        cs.shortform_like_count AS like_count
    FROM 
        content_shortform cs
    JOIN 
        product_info pi ON cs.product_id = pi.product_id
    WHERE 
        cs.product_id IS NOT NULL
        AND NOT EXISTS (
            SELECT 1
            FROM ecommerce_order eo
            WHERE eo.product_id = cs.product_id
              AND eo.order_status = 'PAYMENT_COMPLETE'
        )
    ORDER BY 
        cs.shortform_like_count DESC
    LIMIT #{size} OFFSET #{offset}
""")
    List<ProductKatteRecommendBean> getKatteRecommendedProducts(
            @Param("offset") int offset,
            @Param("size") int size);

    @Select("""
    SELECT 
        pi.*
    FROM 
        product_info pi
    JOIN 
        ecommerce_order eo ON pi.product_id = eo.product_id
    WHERE 
        pi.brand_name = #{brand_name}
    GROUP BY 
        pi.product_id
    ORDER BY 
        COUNT(eo.id) DESC
    LIMIT #{size} OFFSET #{offset}
""")
    List<ProductInfoBean> getTopProductsByBrandOrderCount(
            @Param("brand_name") String brand_name,
            @Param("offset") int offset,
            @Param("size") int size
    );

    @Select("""
    SELECT *
    FROM product_info
    WHERE brand_name = #{brand_name}
    ORDER BY RAND()
    LIMIT #{size}
""")
    List<ProductInfoBean> getRandomProductsByBrand(
            @Param("brand_name") String brand_name,
            @Param("size") int size
    );

    // 현재 보고있는 페이지의 상품과 함께 조회됐던 상품들(전부. 무한 스크롤)
    @Select("""
    SELECT pi.*
    FROM product_info pi
    WHERE pi.product_id IN (
        SELECT DISTINCT upvh.product_id
        FROM user_product_view_history upvh
        WHERE upvh.user_id = #{user_id}
          AND upvh.product_id != #{current_product_id}
    )
    ORDER BY pi.product_id DESC
""")
    List<ProductInfoBean> getAlsoViewedProducts(
            @Param("user_id") int user_id,
            @Param("current_product_id") int current_product_id
    );

    //브랜드 추가
    @Insert("INSERT INTO product_brand (brand_name) VALUES (#{brandName})")
    void insertBrand(String brandName);

    //브랜드 조회
    @Select("SELECT COUNT(*) FROM product_brand WHERE brand_name = #{brandName}")
    boolean isBrandExists(String brandName);

    //상품 판매 등록
    @Insert("""
    INSERT INTO product_per_sale (
        sale_user_id,
        product_id,
        shortform_id,
        auction_data_id
    ) VALUES (
        #{sale_user_id},
        #{product_id},
        #{shortform_id},
        #{auction_data_id}
    )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertPerSale(ProductPerSaleBean perSaleBean);

    //검수 요청 등록
    @Insert("""
    INSERT INTO product_check_result (
        per_sale_id,
        request_user_id,
        check_user_id,
        check_desc,
        check_start_day,
        check_step,
        sale_step
    ) VALUES (
        #{per_sale_id},
        #{request_user_id},
        #{check_user_id},
        #{check_desc},
        NOW(),
        #{check_step},
        #{sale_step}
    )
    """)
    void insertCheckResult(ProductCheckResultBean checkResultBean);

    //최신 product_per_sale의 id를 반환
    @Select("SELECT id FROM product_per_sale ORDER BY id DESC LIMIT 1")
    Integer getLatestPerSaleId();

    //검수 요청으로 인한 판매상태 업데이트
    @Update("""
        UPDATE product_per_sale
        SET sale_step = #{sale_step}
        WHERE id = #{per_sale_id}
    """)
    void updateSaleStepInPerSale(@Param("per_sale_id") int per_sale_id,
                                 @Param("sale_step") ProductCheckResultBean.SaleStep sale_step);

    //판매 완료 요청 업데이트
    @Update("""
    UPDATE product_check_result
    SET sale_step = 'sold_out'
    WHERE id = #{check_result_id}
      AND sale_step = 'on_sale'
    """)
    void markAsSoldOut(@Param("check_result_id") int checkResultId);

    //브랜드 좋아요 여부 확인
    @Select("SELECT COUNT(*) FROM product_brand_like WHERE brand_id = #{brand_id} AND user_id = #{user_id}")
    boolean hasBrandLike(int brand_id, int user_id);

    //브랜드 좋아요 추가
    @Insert("INSERT INTO product_brand_like(brand_id, user_id, created_at) VALUES(#{brand_id}, #{user_id}, NOW())")
    void addBrandLike(int brand_id, int user_id);

    //브랜드 좋아요 삭제
    @Delete("DELETE FROM product_brand_like WHERE brand_id = #{brand_id} AND user_id = #{user_id}")
    void removeBrandLike(int brand_id, int user_id);

    @Update("UPDATE product_brand SET brand_like_count = brand_like_count + 1 WHERE id = #{brand_id}")
    void increaseBrandLikeCount(int brand_id);

    @Update("UPDATE product_brand SET brand_like_count = brand_like_count - 1 WHERE id = #{brand_id} AND brand_like_count > 0")
    void decreaseBrandLikeCount(int brand_id);

    //product like
    @Select("SELECT COUNT(*) FROM product_like WHERE product_id = #{product_id} AND user_id = #{user_id}")
    boolean hasProductLike(int product_id, int user_id);

    @Insert("INSERT INTO product_like(product_id, user_id, created_at) VALUES(#{product_id}, #{user_id}, NOW())")
    void addProductLike(int product_id, int user_id);

    @Delete("DELETE FROM product_like WHERE product_id = #{product_id} AND user_id = #{user_id}")
    void removeProductLike(int product_id, int user_id);

    @Update("UPDATE product_info SET product_like_count = product_like_count + 1 WHERE product_id = #{product_id}")
    void increaseProductLikeCount(int product_id);

    @Update("UPDATE product_info SET product_like_count = product_like_count - 1 WHERE product_id = #{product_id} AND product_like_count > 0")
    void decreaseProductLikeCount(int product_id);

    //상품 사이즈 사이즈 값 조회(동기화 쿼리)
    @Select("SELECT size_value FROM product_size WHERE id = #{id}")
    String getSizeValueById(int id);

    //등록 상품 리스트 조회
    @Select("""
    SELECT * FROM registered_product_view
    ORDER BY check_start_day DESC
    LIMIT #{size} OFFSET #{offset}
    """)
    List<RegisteredProductViewBean> getRegisteredProductList(
            @Param("offset") int offset,
            @Param("size") int size
    );

    //등록 상품 수 조회
    @Select("SELECT COUNT(*) FROM registered_product_view")
    int getRegisteredProductCount();

    //판매 완료 상품 리스트 조회
    @Select("""
    SELECT
        p.product_name,
        cs.title,
        u.email_id AS buyer_email_id,
        ad.auction_end_time,
        ad.is_settle_amount
    FROM auction_data ad
    JOIN product_per_sale ps ON ad.id = ps.auction_data_id
    JOIN product_info p ON ps.product_id = p.product_id
    JOIN content_shortform cs ON ps.shortform_id = cs.id
    JOIN user_info u ON ps.sale_user_id = u.user_id
    JOIN product_check_result pcr ON pcr.per_sale_id = ps.id
    WHERE pcr.sale_step = 'sold_out'
    ORDER BY ad.auction_end_time DESC
    LIMIT #{size} OFFSET #{offset}
    """)
    List<InspectionProductViewBean> getSoldOutList(@Param("offset") int offset, @Param("size") int size);

    //판매 완료 상품 수
    @Select("""
    SELECT COUNT(*)
    FROM product_check_result pcr
    WHERE pcr.sale_step = 'sold_out'
    """)
    int getSoldOutCount();

    //상품 id + 사이즈값으로 사이즈 조회
    @Select("SELECT id FROM product_size WHERE product_id = #{productId} AND size_value = #{sizeValue}")
    Integer getSizeId(@Param("productId") int productId, @Param("sizeValue") String sizeValue);

    @Select("SELECT id FROM product_size ORDER BY id DESC LIMIT 1")
    Integer selectLatestSizeId();

    //경매 시간 세팅을 위한 mapper
    @Select("SELECT * FROM product_check_result WHERE id = #{id}")
    ProductCheckResultBean getCheckResultById(@Param("id") int id);

    //경매 시간 세팅을 위한
    @Select("SELECT auction_data_id FROM product_per_sale WHERE id = #{perSaleId}")
    int getAuctionIdByPerSaleId(@Param("perSaleId") int perSaleId);

    //관심상품등록.
    @Insert("""
    INSERT INTO `product_like` (user_id, product_id, created_at)
    VALUES (#{user_id}, #{product_id}, NOW())""")
    int insertWishlist(@Param("user_id") int userId, @Param("product_id") int productId);

    //관심상품조회
    @Select("""
    SELECT COUNT(*) 
    FROM `product_like`
    WHERE user_id = #{user_id} AND product_id = #{product_id}""")
    int countWishlistItem(@Param("user_id") int userId, @Param("product_id") int productId);

}
