package me.soldesk.katteproject_backend.mapper;

import common.bean.UserBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user_info (" +
            "email_id, password, first_name, second_name, nickname, " +
            "phone_number, birth_date, profile_url, is_admin" +
            ") VALUES (" +
            "#{email_id}, #{password}, #{first_name}, #{second_name}, #{nickname}, " +
            "#{phone_number}, #{birth_date}, #{profile_url}, #{is_admin}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "user_id")  // ✅ 이 부분이 핵심!
    void addUserInfo(UserBean user);
}