package me.soldesk.katteproject_backend.mapper;

import common.bean.content.ContentStyleBean;
import common.bean.product.ProductInfoBean;
import me.soldesk.katteproject_backend.test.ProductINFO_TEST;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SearchMapper {
    @Select("""
        SELECT * FROM product_info 
        WHERE product_name COLLATE utf8mb3_general_ci LIKE CONCAT('%', #{keyword}, '%')
           OR product_desc COLLATE utf8mb3_general_ci LIKE CONCAT('%', #{keyword}, '%')
           OR product_name_kor COLLATE utf8mb3_general_ci LIKE CONCAT('%', #{keyword}, '%')
           OR brand_name COLLATE utf8mb3_general_ci LIKE CONCAT('%', #{keyword}, '%')
    """)
    List<ProductInfoBean> searchProductByKeyword(@Param("keyword") String keyword);

    @Select("""
            SELECT * FROM content_style 
            WHERE style_title COLLATE utf8mb3_general_ci LIKE CONCAT('%', #{keyword}, '%')
            OR caption COLLATE utf8mb3_general_ci LIKE CONCAT('%', #{keyword}, '%')
            """)
    List<ContentStyleBean> searchStyleByKeyword(String keyword);
}
