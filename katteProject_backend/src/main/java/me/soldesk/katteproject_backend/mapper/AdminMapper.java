package me.soldesk.katteproject_backend.mapper;


import common.bean.user.UserBanBean;
import common.bean.user.UserRestrictionBean;
import org.apache.ibatis.annotations.*;

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
            ORDER BY end_date DESC
            LIMIT 1
            """)
    UserRestrictionBean findValidRestrictionByUserId(@Param("user_id") int user_id);

}
