package me.soldesk.katteproject_backend.mapper;

import common.bean.content.ContentShortformBean;
import common.bean.content.ContentStyleBean;
import common.bean.content.ContentStyleComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ContentMapper {
    @Insert("INSERT INTO content_shortform (\n" +
            "    product_id,\n" +
            "    author_id,\n" +
            "    live_content_id,\n" +
            "    title,\n" +
            "    description,\n" +
            "    total_view,\n" +
            "    current_view_count,\n" +
            "    shortform_like_count,\n" +
            "    content_url\n" +
            ") VALUES (\n" +
            "    #{product_id},                         -- product_id\n" +
            "    #{author_id},                           -- author_id (user_info.user_id 참조)\n" +
            "    #{live_content_id},                        -- live_content_id\n" +
            "    #{title},              -- title\n" +
            "    #{description},  -- description\n" +
            "    #{total_view},                        -- total_view\n" +
            "    #{current_view_count},                         -- current_view_count\n" +
            "    #{shortform_like_count},                          -- shortform_like_count\n" +
            "    #{content_url}   -- content_url\n" +
            ")")
    void addShortform(ContentShortformBean contentShortformBean);

    @Select("SELECT * FROM content_shortform WHERE id = #{id};")
    ContentShortformBean getShortformById(int id);

    @Select("SELECT * FROM content_shortform WHERE product_id = #{productId} LIMIT #{count} OFFSET #{offset}")
    List<ContentShortformBean> getShortformByProductId(int productId, int count, int offset);

    @Insert("""
    INSERT INTO content_style (
        style_title,
        user_id,
        caption,
        created_date,
        img_count
    ) VALUES (
        #{style_title},
        #{user_id},
        #{caption},
        NOW(),
        #{img_count}
    )
""")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addStyle(ContentStyleBean contentStyleBean);

    @Insert("INSERT INTO content_hashtag (hashtag_name) VALUES (#{hashtag})")
    void addHashtag(String hashtag);

    @Select("SELECT id FROM content_hashtag WHERE hashtag_name = #{hashtag}")
    Integer getHashtagIdByName(String hashtag);

    @Insert("INSERT INTO content_join_hashtag (content_id, hashtag_id)\n" +
            "VALUES (#{contentId}, #{hashtagId});")
    void addStyleJoinHashTag(int contentId, int hashtagId);

    /**
     * 스타일 ID로 연결된 해시태그 이름 목록 조회
     */
    @Select("""
        SELECT h.hashtag_name
          FROM content_join_hashtag ch
          JOIN content_hashtag h ON ch.hashtag_id = h.id
         WHERE ch.content_id = #{styleId}
    """)
    List<String> getHashtagsByStyleId(int styleId);

    @Select("SELECT c.*\n" +
            "FROM content_style c\n" +
            "JOIN content_join_hashtag ch ON c.id = ch.content_id\n" +
            "JOIN content_hashtag h ON ch.hashtag_id = h.id\n" +
            "WHERE h.hashtag_name = #{hashtag}\n" +
            "LIMIT #{count} OFFSET #{offset};")
    @Results({
            @Result(column="id",           property="id"),
            @Result(column="style_title",  property="style_title"),
            @Result(column="user_id",      property="user_id"),
            @Result(column="caption",      property="caption"),
            @Result(column="created_date", property="created_date"),
            @Result(column="img_count",    property="img_count"),
            @Result(
                    column="id",
                    property="hashtags",
                    javaType=List.class,
                    many=@Many(select="me.soldesk.katteproject_backend.mapper.ContentMapper.getHashtagsByStyleId")
            )
    })
    List<ContentStyleBean> getStyleByHashtag(String hashtag, int count, int offset);

    @Select("SELECT * FROM content_style WHERE id = #{id}")
    @Results({
            @Result(column="id",           property="id"),
            @Result(column="style_title",  property="style_title"),
            @Result(column="user_id",      property="user_id"),
            @Result(column="caption",      property="caption"),
            @Result(column="created_date", property="created_date"),

            @Result(column="img_count",    property="img_count"),
            @Result(
                    column="id",
                    property="hashtags",
                    javaType=List.class,
                    many=@Many(select="me.soldesk.katteproject_backend.mapper.ContentMapper.getHashtagsByStyleId")
            )
    })
    ContentStyleBean getStyleById(int id);

    @Select("SELECT * FROM content_style WHERE user_id = #{userId} LIMIT #{count} OFFSET #{offset}")
    @Results({
            @Result(column="id",           property="id"),
            @Result(column="style_title",  property="style_title"),
            @Result(column="user_id",      property="user_id"),
            @Result(column="caption",      property="caption"),
            @Result(column="created_date", property="created_date"),
            @Result(column="img_count",    property="img_count"),
            @Result(
                    column="id",
                    property="hashtags",
                    javaType=List.class,
                    many=@Many(select="me.soldesk.katteproject_backend.mapper.ContentMapper.getHashtagsByStyleId")
            )
    })
    List<ContentStyleBean> getStyleByUserId(
            @Param("userId") int userId,
            @Param("count")  int count,
            @Param("offset") int offset
    );

    @Select(("SELECT * FROM content_stylecomment WHERE style_id = #{styleId}"))
    List<ContentStyleComment> getStyleCommentByStyleId(int styleId);

    @Insert("INSERT INTO content_stylecomment (style_id, user_id, recomment_id, content, create_at)\n" +
            "VALUES (#{style_id}, #{user_id}, #{recomment_id}, #{content}, #{create_at})")
    void addStyleComment(ContentStyleComment contentStyleComment);

    @Select("SELECT * FROM content_stylecomment WHERE user_id = #{userId} LIMIT #{count} OFFSET #{offset}")
    List<ContentStyleComment> getStyleCommentByUserId(int userId, int count, int offset);

    @Select("SELECT * FROM content_style ORDER BY created_date DESC LIMIT #{size} OFFSET #{offset}")
    @Results({
            @Result(column="id",           property="id"),
            @Result(column="style_title",  property="style_title"),
            @Result(column="user_id",      property="user_id"),
            @Result(column="caption",      property="caption"),
            @Result(column="created_date", property="created_date"),
            @Result(column="img_count",    property="img_count"),
            @Result(
                    column="id",
                    property="hashtags",
                    javaType=List.class,
                    many=@Many(select="me.soldesk.katteproject_backend.mapper.ContentMapper.getHashtagsByStyleId")
            )
    })
    List<ContentStyleBean> getRecentStyles(
            @Param("size")   int size,
            @Param("offset") int offset
    );

    @Select("SELECT COUNT(*) FROM content_style WHERE user_id = #{userId}")
    int countStyleByUserId(int userId);

    @Select("SELECT COUNT(*) FROM content_stylecomment WHERE user_id = #{userId}")
    int countStyleCommentByUserId(int userId);

    @Select("SELECT LAST_INSERT_ID()")
    int getLastInsertId();

    @Select("SELECT COUNT(*) FROM content_stylelike WHERE style_id = #{styleId} AND user_id = #{userId}")
    boolean hasStyleLike(int styleId, int userId);

    @Insert("INSERT INTO content_stylelike(style_id, user_id, created_at) VALUES(#{styleId}, #{userId}, NOW())")
    void addStyleLike(int styleId, int userId);

    @Delete("DELETE FROM content_stylelike WHERE style_id = #{styleId} AND user_id = #{userId}")
    void removeStyleLike(int styleId, int userId);

    @Update("UPDATE content_style SET like_count = like_count + 1 WHERE id = #{styleId}")
    void increaseStyleLikeCount(int styleId);

    @Update("UPDATE content_style SET like_count = like_count - 1 WHERE id = #{styleId} AND like_count > 0")
    void decreaseStyleLikeCount(int styleId);

    @Select("""
            SELECT c.*
              FROM content_style c
              JOIN content_stylelike l ON c.id = l.style_id
             WHERE l.user_id = #{userId}
             ORDER BY l.created_at DESC
             LIMIT #{count} OFFSET #{offset}
            """)
    @Results({
            @Result(column="id",           property="id"),
            @Result(column="style_title",  property="style_title"),
            @Result(column="user_id",      property="user_id"),
            @Result(column="caption",      property="caption"),
            @Result(column="created_date", property="created_date"),
            @Result(column="img_count",    property="img_count"),
            @Result(
                    column="id",
                    property="hashtags",
                    javaType=List.class,
                    many=@Many(select="me.soldesk.katteproject_backend.mapper.ContentMapper.getHashtagsByStyleId")
            )
    })
    List<ContentStyleBean> getLikedStylesByUser(
            @Param("userId") int userId,
            @Param("count")  int count,
            @Param("offset") int offset
    );

    @Select("SELECT COUNT(*) FROM content_short_like WHERE short_id = #{short_id} AND user_id = #{user_id}")
    boolean hasShortLike(int short_id, int user_id);

    @Insert("INSERT INTO content_short_like(short_id, user_id, created_at) VALUES(#{short_id}, #{user_id}, NOW())")
    void addShortLike(int short_id, int user_id);

    @Delete("DELETE FROM content_short_like WHERE short_id = #{short_id} AND user_id = #{user_id}")
    void removeShortLike(int short_id, int user_id);

    @Update("UPDATE content_shortform SET shortform_like_count = shortform_like_count + 1 WHERE id = #{short_id}")
    void increaseShortLikeCount(int short_id);

    @Update("UPDATE content_shortform SET shortform_like_count = shortform_like_count - 1 WHERE id = #{short_id} AND shortform_like_count > 0")
    void decreaseShortLikeCount(int short_id);

    @Select("SELECT * FROM content_shortform ORDER BY RAND() LIMIT 1")
    ContentShortformBean getRandomShort();
}
