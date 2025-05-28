package me.soldesk.katteproject_backend.mapper;

import common.bean.user.UserAddressBean;
import common.bean.user.UserBean;
import common.bean.user.UserPaymentBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user_info (" +
            "email_id, password, first_name, second_name, nickname, " +
            "phone_number, birth_date, introduce_desc, profile_url, is_admin" +
            ") VALUES (" +
            "#{email_id}, #{password}, #{first_name}, #{second_name}, #{nickname}, " +
            "#{phone_number}, #{birth_date}, #{introduce_desc}, #{profile_url}, #{is_admin}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "user_id")
    void addUserInfo(UserBean user);

    @Select("SELECT * FROM user_info WHERE user_id = #{user_id}")
    UserBean getUserInfoById(int user_id);

    @Select("SELECT * FROM user_info WHERE email_id = #{email_id}")
    UserBean getUserInfoByEmail(String email_id);

    @Update("UPDATE user_info SET email_id = #{email_id} WHERE user_id = #{user_id}")
    void updateUserInfoMail(int user_id, String email_id);

    @Update("UPDATE user_info SET password = #{password} WHERE user_id = #{user_id}")
    void updateUserInfoPassword(int user_id, String password);

    @Update("UPDATE user_info SET nickname = #{nickname} WHERE user_id = #{user_id}")
    void updateUserInfoNickname(int user_id, String nickname);

    @Update("UPDATE user_info SET phone_number = #{phone} WHERE user_id = #{user_id}")
    void updateUserInfoPhone(int user_id, String phone);

    @Update("UPDATE user_info SET introduce_desc = #{introduce_desc} WHERE user_id = #{user_id}")
    void updateUserInfoIntroduce(int user_id, String introduce_desc);

    @Update("UPDATE user_info SET profile_url = #{profile_url} WHERE user_id = #{user_id}")
    void updateUserInfoProfileUrl(int user_id, String profile_url);

    @Delete("DELETE FROM user_info WHERE user_id = #{user_id}")
    void deleteUserInfoById(int user_id);

    @Select("SELECT COUNT(*) FROM user_info WHERE email_id = #{email_id} AND password = #{password}")
    boolean existLoginByEmailAndPassword(String email_id, String password);

    @Insert("INSERT INTO user_payment (" +
            "user_id, " +
            "point, " +
            "katte_money, " +
            "locked_deposit_katte_money" +
            ") VALUES (" +
            "#{user_id}, " +
            "#{point}, " +
            "#{katte_money}, " +
            "0" +  // 잠금 금액은 기본값 0
            ")")
    void createDefaultUserPayment(int user_id, int point, int katte_money);

    @Select("SELECT * FROM user_payment WHERE user_id = #{user_id}")
    UserPaymentBean getUserPaymentById(int user_id);

    @Insert("""
        INSERT INTO user_address (
        user_id,
        name,
        phone_number,
        address_line01,
        address_line02,
        is_main
    ) VALUES (
        #{user_id},                              -- user_id (user_info 테이블에 존재하는 유저 ID)
        #{name},                       -- name
        #{phone_number},               -- phone_number
        #{address_line01}, -- address_line01
        #{address_line02},                 -- address_line02
        #{is_main}                           -- is_main
    );
    """)
    void addUserAddress(UserAddressBean userAddressBean);

    @Select("""
            SELECT * FROM user_address
            WHERE user_id = #{user_id}
            """)
    List<UserAddressBean> getUserAddresses(int user_id);

    @Select("""
        SELECT * FROM user_address
        WHERE user_id = #{user_id}
        AND is_main = TRUE
        """)
    UserAddressBean getUserMainAddress(int user_id);

    @Update("""
        UPDATE user_address
        SET is_main = FALSE
        WHERE user_id = #{user_id}
    """)
    void resetMainAddress(int user_id);

    @Update("""
        UPDATE user_address
        SET is_main = TRUE
        WHERE id = #{address_id} AND user_id = #{user_id}
    """)
    void setMainAddress(int user_id, int address_id);
}