package me.soldesk.katteproject_backend.mapper;

import common.bean.user.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("""
        INSERT INTO user_info (
            email_id, password, first_name, second_name, nickname,
            phone_number, birth_date, introduce_desc, profile_url, is_admin
        ) VALUES (
            #{email_id}, #{password}, #{first_name}, #{second_name}, #{nickname},
            #{phone_number}, #{birth_date}, #{introduce_desc}, #{profile_url}, #{is_admin}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "user_id")
    void insertUser(UserBean user);

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
    //주소 추가
    @Insert("""
        INSERT INTO user_address (
        user_id,
        name,
        phone_number,
        address_line01,
        address_line02,
        is_main,
        post_num
    ) VALUES (
        #{user_id},                              -- user_id (user_info 테이블에 존재하는 유저 ID)
        #{name},                       -- name
        #{phone_number},               -- phone_number
        #{address_line01}, -- address_line01
        #{address_line02},                 -- address_line02
        #{is_main},                           -- is_main
        #{post_num}
    );
    """)
    void addUserAddress(UserAddressBean userAddressBean);

    //유저 id로 주소 조회 (Main주소가 아닌 애만 나옴.)
    @Select("""
            SELECT * FROM user_address
            WHERE user_id = #{user_id} AND is_main = 0
            """)
    List<UserAddressBean> getUserAddresses(int user_id);

    //유저id, id로 주소 상세 조회
    @Select("""
            SELECT * FROM user_address
            WHERE user_id = #{user_id} AND id = #{id}
            """)
    List<UserAddressBean> getUserAddressDetail(int user_id, int id);

    //유저 id랑 메인 주소여부로 주소 조회
    @Select("""
        SELECT * FROM user_address
        WHERE user_id = #{user_id}
        AND is_main = TRUE
        """)
    UserAddressBean getUserMainAddress(int user_id);

    //유저 id 받고 메인 주소 해제
    @Update("""
        UPDATE user_address
        SET is_main = FALSE
        WHERE user_id = #{user_id}
    """)
    void resetMainAddress(int user_id);
    //주소id랑 유저id 받고 메인 주소 설정
    @Update("""
        UPDATE user_address
        SET is_main = TRUE
        WHERE id = #{address_id} AND user_id = #{user_id}
    """)
    void setMainAddress(int user_id, int address_id);

    //유저 주소 변경
    @Update("""
    UPDATE user_address SET
        name = #{name},
        phone_number = #{phone_number},
        address_line01 = #{address_line01},
        address_line02 = #{address_line02},
        post_num = #{post_num}
        WHERE id = #{id} AND user_id = #{user_id}
        """)
    int editAddress(UserAddressBean userAddressBean);

    //유저 주소 삭제
    @Delete("""
        DELETE FROM user_address
        WHERE id = #{address_id} AND user_id = #{user_id}

    """)
    int deleteAddress(int user_id, int address_id);

    @Select("SELECT katte_money FROM user_payment WHERE user_id = #{user_id}")
    int selectKatteMoney(int user_id);

    @Update("UPDATE user_payment SET katte_money = #{katte_money} WHERE user_id = #{user_id}")
    void updateKatteMoney(int user_id, int katte_money);

    @Insert("INSERT INTO user_katte_money_log " +
            "(user_id, change_amount, reason, created_at) " +
            "VALUE (#{user_id}, #{change_amount}, #{reason}, NOW())")
    void addKatteMoneyLog(UserKatteMoneyLogBean userKatteMoneyLogBean);

    @Insert("INSERT INTO user_katte_money_refund " +
            "(user_id, amount, account_number, bank_type) " +
            "VALUES (#{user_id}, #{amount}, #{account_number}, #{bank_type})")
    void addKatteMoneyRefund(UserKatteMoneyRefundBean userKatteMoneyRefundBean);

    @Select("SELECT * FROM user_katte_money_refund WHERE user_id = #{user_id}")
    List<UserKatteMoneyRefundBean> getKatteMoneyRefunds(int user_id);

    @Update("UPDATE user_katte_money_refund SET status = #{status} WHERE id = #{refund_id}")
    void updateKatteMoneyRefund(@Param("status") String status, @Param("refund_id") int refund_id);

    @Insert("""
        INSERT INTO user_agreeterms (
        user_id,
        terms_code,
        is_agreed,
        agreed_at
        ) VALUES (
        #{user_id},                              -- user_id (user_info 테이블에 존재하는 유저 ID)
        #{terms_code},                       -- name
        #{is_agreed},               -- phone_number
        NOW()
        );
            """)
    void addUserTerm(int user_id, int terms_code, boolean is_agreed);

    @Select("""
            SELECT * FROM user_info WHERE nickname = #{nickname}
            """)
    UserBean getUserByNickname(String nickname);

    @Select("""
            SELECT * FROM user_info WHERE email_id = #{email_id}
            """)
    UserBean getUserByEmail(String email_id);


    @Update("UPDATE user_payment SET katte_money = katte_money - #{amount} WHERE user_id = #{userId}")
    void subtractKatteMoney(@Param("userId") int userId, @Param("amount") int amount);


}