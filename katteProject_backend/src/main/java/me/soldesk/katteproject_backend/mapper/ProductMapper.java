package me.soldesk.katteproject_backend.mapper;

import common.bean.product.ProductInfoBean;
import common.bean.product.ProductSizeBean;
import common.bean.product.ProductCheckResultBean;
import common.bean.product.ProductPerSaleBean;
import common.bean.admin.InspectionProductViewBean;
import common.bean.admin.RegisteredProductViewBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    //제품 등록
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

    //제품 정보 수정
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

    // 상품 1개 상세 조회
    @Select("SELECT * FROM product_info WHERE product_id = #{product_id}")
    ProductInfoBean getProductById(@Param("product_id") int product_id);

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

    //검수 요청
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

    //검수 요청으로 인한 상태 업데이트
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

    //brand like
    @Select("SELECT COUNT(*) FROM product_brand_like WHERE brand_id = #{brand_id} AND user_id = #{user_id}")
    boolean hasBrandLike(int brand_id, int user_id);

    @Insert("INSERT INTO product_brand_like(brand_id, user_id, created_at) VALUES(#{brand_id}, #{user_id}, NOW())")
    void addBrandLike(int brand_id, int user_id);

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

    //상품 사이즈 동기화 쿼리
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
}
