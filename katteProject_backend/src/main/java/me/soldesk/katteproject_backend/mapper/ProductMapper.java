package me.soldesk.katteproject_backend.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductMapper {
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
}
