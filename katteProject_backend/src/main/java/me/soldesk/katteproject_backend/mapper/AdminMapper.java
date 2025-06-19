package me.soldesk.katteproject_backend.mapper;


import common.bean.admin.SoldoutProductViewBean;
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

    //특정 제한 해제
    @Delete("DELETE FROM user_restriction WHERE user_id = #{userId} AND restriction_type = #{restrictionType}")
    void deleteRestrictionByType(@Param("userId") int userId, @Param("restrictionType") String restrictionType);

    //제한 해제
    @Delete("DELETE FROM user_restriction WHERE user_id = #{userId}")
    void deleteAllRestrictions(@Param("userId") int userId);

    //제한 수정
    @Update("""
            UPDATE user_restriction
            SET stop_days = #{stop_days},
            end_date = DATE_ADD(start_date, INTERVAL #{stop_days} DAY)
            WHERE user_id = #{user_id}
            """)
    void updateUserRestriction(@Param("user_id") int user_id, @Param("stop_days") int stop_days);

    //user_id 로 조회
    @Select("SELECT COUNT(*) > 0 FROM user_ban WHERE user_id = #{user_id}")
    boolean isUserBanned(@Param("user_id") int userId);

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

    //신필터
    @Select("SELECT * FROM user_admin_view LIMIT #{size} OFFSET #{offset}")
    List<UserAdminViewBean> getAllUsers(@Param("offset") int offset, @Param("size") int size);

    //상태 처리 분기
    @Select("""
    SELECT * FROM user_admin_view
    WHERE ban_status = '계정 정지' OR restriction_status IS NOT NULL
    LIMIT #{size} OFFSET #{offset}
    """)
    List<UserAdminViewBean> getReportedUsers(@Param("offset") int offset, @Param("size") int size);


    //회원 검색(페이징 처리 추가)
    @Select("""
    SELECT * FROM user_admin_view
    WHERE 
        nickname LIKE CONCAT('%', #{keyword}, '%')
        OR email_id LIKE CONCAT('%', #{keyword}, '%')
        OR CAST(user_id AS CHAR) = #{keyword}
    LIMIT #{size} OFFSET #{offset}
    """)
    List<UserAdminViewBean> searchUsersByKeyword(@Param("keyword") String keyword,
                                                 @Param("offset") int offset,
                                                 @Param("size") int size);

    //검색된 회원 수 처리
    @Select("""
    SELECT COUNT(*) FROM user_admin_view
    WHERE 
        nickname LIKE CONCAT('%', #{keyword}, '%')
        OR email_id LIKE CONCAT('%', #{keyword}, '%')
        OR CAST(user_id AS CHAR) = #{keyword}
    """)
    int countUsersByKeyword(@Param("keyword") String keyword);

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
    SET check_step = 'COMPLETED',
        sale_step = 'ON_SALE',
        check_end_day = NOW()
    WHERE id = #{check_result_id}
    """)
    void markInspectionComplete(@Param("check_result_id") int checkResultId);

    //검수 상태 변경(조건 미달)
    @Update("""
    UPDATE product_check_result
    SET check_step = 'RETURNED',
        sale_step = 'INSPECTION_FAIL',
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

    //검수 실패 시 검수일로부터 3일 후 데이터 자동으로 삭제
    @Delete("""
    DELETE FROM product_check_result
    WHERE sale_step = 'INSPECTION_FAIL'
      AND check_end_day IS NOT NULL
      AND check_end_day < NOW() - INTERVAL 3 DAY
    """)
    int deleteInspectionFailedOld();

    //판매 기간 만료 시 판매 만료일로부터 3일 후 데이터 자동으로 삭제
    @Delete("""
    DELETE FROM product_per_sale
    WHERE id IN (
      SELECT * FROM (
        SELECT ps.id
        FROM product_per_sale ps
        JOIN product_check_result pcr ON pcr.per_sale_id = ps.id
        JOIN auction_data ad ON ps.auction_data_id = ad.id
        WHERE pcr.sale_step = 'EXPIRED'
          AND ad.auction_end_time < NOW() - INTERVAL 3 DAY
      ) AS temp
    )
    """)
    int deleteExpiredOld();

    //판매 만료 상품 삭제 눌렀을때 3일전으로 땡겨서 바로 삭제처리 되도록 조건 업데이트
    @Update("""
    UPDATE auction_data
    SET auction_end_time = NOW() - INTERVAL 3 DAY
    WHERE id = #{auction_id}
""")
    void forceExpireAuctionEndTime(@Param("auction_id") int auctionId);

    //수동 판매만료 삭제
    @Delete("""
        DELETE FROM product_per_sale
        WHERE id IN (
            SELECT ps.id
            FROM product_per_sale ps
            JOIN product_check_result pcr ON pcr.per_sale_id = ps.id
            JOIN auction_data ad ON ps.auction_data_id = ad.id
            WHERE ad.id = #{auction_id}
              AND pcr.sale_step = 'EXPIRED'
              AND ad.auction_end_time < NOW() - INTERVAL 3 DAY
        )
    """)
    int deleteExpiredByAuctionId(@Param("auction_id") int auctionId);

    //판매 기간 만료 자동 스캐줄링 쿼리
    @Update("""
        UPDATE product_check_result pcr
        JOIN product_per_sale ps ON pcr.per_sale_id = ps.id
        JOIN auction_data ad ON ps.auction_data_id = ad.id
        SET pcr.sale_step = 'EXPIRED'
        WHERE pcr.sale_step = 'ON_SALE'
          AND ad.auction_end_time < NOW()
    """)
    void updateExpiredSales();

    //판매 완료 리스트 조회
    @Select("""
    SELECT * FROM soldout_product_view
    ORDER BY ordered_at DESC
    LIMIT #{size} OFFSET #{offset}
    """)
    List<SoldoutProductViewBean> getSoldOutProductList(
            @Param("offset") int offset,
            @Param("size") int size
    );

    //판매 완료 수 조회
    @Select("SELECT COUNT(*) FROM soldout_product_view")
    int getSoldOutProductCount();

}
