package me.soldesk.katteproject_backend.mapper;

import common.bean.content.ContentStyleBean;
import me.soldesk.katteproject_backend.test.ProductINFO_TEST;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SearchMapper {
    @Select("""
           SELECT * FROM product_info 
           WHERE product_name LIKE #{keyword} 
           OR product_desc LIKE #{keyword}
           OR brand_name LIKE #{keyword}
           """)
    List<ProductINFO_TEST> searchProductByKeyword(String keyword);

    @Select("""
            SELECT * FROM content_style 
            WHERE style_title LIKE #{keyword} 
            OR caption LIKE #{keyword}
            """)
    List<ContentStyleBean> searchStyleByKeyword(String keyword);
}
