package me.soldesk.katteproject_backend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

import common.bean.content.ContentStyleBean;

@Mapper
public interface StyleMapper {
    /** 전체 스타일 게시물 개수 조회 */
    @Select("SELECT COUNT(*) FROM content_style")
    int countAllStyles();

    /** offset, limit 기반으로 최신순 조회 (목록) */
    @Select("""
        SELECT *
          FROM content_style
         ORDER BY created_date DESC
        """)
    List<ContentStyleBean> selectRecentStyles(RowBounds rowBounds);

    /** 이미지 URL 리스트 조회 */
    @Select("""
        SELECT image_url
          FROM content_styleimage
         WHERE style_id = #{styleId}
      ORDER BY sort_order
        """)
    List<String> selectImageUrlsByStyleId(@Param("styleId") int styleId);

    /** 단건 스타일(Detail) 조회 */
    @Select("""
        SELECT id,
               style_title,
               user_id,
               caption,
               created_date,
               like_count
          FROM content_style
         WHERE id = #{id}
        """)
    ContentStyleBean selectStyleById(@Param("id") int id);

    /** 단건 스타일의 해시태그 조회 */
    @Select("""
        SELECT hashtag
          FROM content_style_hashtag
         WHERE style_id = #{styleId}
        """)
    List<String> selectHashtagsByStyleId(@Param("styleId") int styleId);
}
