package me.soldesk.katteproject_backend.mapper;


import common.bean.user.UserBanBean;
import common.bean.user.UserRestrictionBean;
import common.bean.admin.InspectionProductViewBean;
import common.bean.admin.UserAdminViewBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {
    //벤 유저 등록
    @Insert("""
                INSERT INTO user_ban (user_id, ban_start)
                VALUES (#{user_id}, #{ban_start})
            """)
    void insertUserBan(UserBanBean userBanBean);

    //벤 해제
    @Delete("DELETE FROM user_ban WHERE user_id = #{user_id}")
    void deleteUserBan(@Param("user_id") int userId);

    //제한 유저 등록
    @Insert("""
                INSERT INTO user_restriction (
                user_id, restriction_type, start_date, end_date, stop_days
                )
                VALUES (
                #{user_id}, #{restriction_type}, #{start_date}, #{end_date}, #{stop_days}
                )
            """)
    void insertUserRestriction(UserRestrictionBean bean);

    //제한 해제
    @Delete("DELETE FROM user_restriction WHERE user_id = #{user_id}")
    void deleteUserRestriction(@Param("user_id") int user_id);

    //제한 수정
    @Update("""
            UPDATE user_restriction
            SET stop_days = #{stop_days},
            end_date = DATE_ADD(start_date, INTERVAL #{stop_days} DAY)
            WHERE user_id = #{user_id}
            """)
    void updateUserRestriction(@Param("user_id") int user_id, @Param("stop_days") int stop_days);

    //유저 상태 조회(정지)
    @Select("SELECT * FROM user_ban WHERE user_id = #{user_id} LIMIT 1")
    UserBanBean findBanByUserId(@Param("user_id") int user_id);

    //유저 상태 조회(제한)
    @Select("""
            SELECT * FROM user_restriction
            WHERE user_id = #{user_id}
            AND end_date > NOW()
            """)
    List<UserRestrictionBean> findAllValidRestrictionsByUserId(@Param("user_id") int user_id);

    //전체 조회 또는 필터 조회
    @Select("""
            <script>
            SELECT * FROM user_admin_view
            <where>
                <if test="filter != null and filter == 'restricted'">
                    (ban_status = '계정 정지' OR restriction_status IS NOT NULL)
                </if>
            </where>
            LIMIT #{size} OFFSET #{offset}
            </script>
            """)
    List<UserAdminViewBean> findUserList(@Param("offset") int offset,
                                         @Param("size") int size,
                                         @Param("filter") String filter);

    //전체 회원 수
    @Select("SELECT COUNT(*) FROM user_admin_view")
    int getUserTotalCount();

    //회원 검색
    @Select("""
                SELECT * FROM user_admin_view
                WHERE 
                    nickname LIKE CONCAT('%', #{keyword}, '%')
                    OR email_id LIKE CONCAT('%', #{keyword}, '%')
                    OR CAST(user_id AS CHAR) = #{keyword}
            """)
    List<UserAdminViewBean> searchUsersByKeyword(@Param("keyword") String keyword);

    //회원 단건 조회
    @Select("""
            SELECT *
            FROM user_admin_view
            WHERE user_id = #{user_id}
            LIMIT 1
            """)
    UserAdminViewBean findUserById(@Param("user_id") int user_id);

    //검수 상태 변경(검수 성공)
    @Update("""
    UPDATE product_check_result
    SET check_step = 'completed',
        sale_step = 'on_sale',
        check_end_day = NOW()
    WHERE id = #{check_result_id}
    """)
    void markInspectionComplete(@Param("check_result_id") int checkResultId);

    //검수 상태 변경(조건 미달)
    @Update("""
    UPDATE product_check_result
    SET check_step = 'returned',
        sale_step = 'inspection_fail',
        check_end_day = NOW()
    WHERE id = #{check_result_id}
    """)
    void markInspectionFail(@Param("check_result_id") int checkResultId);

    //검수 리스트 조회
    @Select("""
    SELECT * FROM inspection_product_view
    ORDER BY check_start_day DESC
    LIMIT #{size} OFFSET #{offset}
    """)
    List<InspectionProductViewBean> getInspectionProductViewList(
            @Param("offset") int offset,
            @Param("size") int size
    );

    //전체 검수 건수
    @Select("SELECT COUNT(*) FROM inspection_product_view")
    int getInspectionTotalCount();

    //검수 삭제 대상 조건에 상태 조건 만족한 3일 이상 지난 검수 데이터 ID 목록 조회
    @Select("""
    SELECT pcr.id
    FROM product_check_result pcr
    JOIN product_per_sale ps ON pcr.per_sale_id = ps.id
    JOIN auction_data ad ON ps.auction_data_id = ad.id
    WHERE (
        pcr.check_step = 'returned' AND DATEDIFF(NOW(), pcr.check_end_day) >= 3
    ) OR (
        pcr.sale_step = 'expired' AND DATEDIFF(NOW(), ad.auction_end_time) >= 3
    )
    """)
    List<Integer> getDeletableCheckResultIds();

    //검수 데이터 삭제
    @Delete("DELETE FROM product_check_result WHERE id = #{id}")
    void deleteCheckResultById(int id);

    @Delete("""
    DELETE FROM product_per_sale
    WHERE id IN (
        SELECT per_sale_id FROM product_check_result
        WHERE id = #{id}
    )
    """)
    void deletePerSaleByCheckResultId(int id);

}
