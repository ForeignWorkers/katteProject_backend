package me.soldesk.katteproject_backend.service;

import common.bean.UserBean;
import common.bean.UserPaymentBean;
import lombok.ToString;
import me.soldesk.katteproject_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void addUserInfo(UserBean joinUserBean) {
        userMapper.addUserInfo(joinUserBean);
        //회원 가입 동시에 페이먼트 테이블 함께 생성
        createDefaultUserPayment(Integer.toString(joinUserBean.getUser_id()));
    }

    public UserBean getUserInfo(String user_id, String email_id) {
        if(user_id != null && !user_id.trim().isEmpty()) {
            return userMapper.getUserInfoById(Integer.parseInt(user_id));
        }else {
            return userMapper.getUserInfoByEmail(email_id);
        }
    }

    public void updateUserInfoEmail(String user_id, String email_id) {
        userMapper.updateUserInfoMail(Integer.parseInt(user_id), email_id);
    }

    public void updateUserInfoPassword(String user_id, String password) {
        userMapper.updateUserInfoPassword(Integer.parseInt(user_id), password);
    }

    public void updateUserInfoPhone(String user_id, String phone) {
        userMapper.updateUserInfoPhone(Integer.parseInt(user_id), phone);
    }

    public void updateUserInfoNickname(String user_id, String nickname) {
        userMapper.updateUserInfoNickname(Integer.parseInt(user_id), nickname);
    }

    public void updateUserInfoIntroduce(String user_id, String introduce) {
        userMapper.updateUserInfoIntroduce(Integer.parseInt(user_id), introduce);
    }

    public void updateUserInfoProfile(String user_id, String profile_url) {
        userMapper.updateUserInfoProfileUrl(Integer.parseInt(user_id), profile_url);
    }

    public void deleteUserInfo(String user_id) {
        userMapper.deleteUserInfoById(Integer.parseInt(user_id));
    }

    public boolean existsUserInfo(String email_id, String password) {
        return userMapper.existLoginByEmailAndPassword(email_id, password);
    }

    public void createDefaultUserPayment(String user_id){
        userMapper.createDefaultUserPayment(Integer.parseInt(user_id), 0,0);
    }

    public UserPaymentBean getUserPayment(String user_id){
        return userMapper.getUserPaymentById(Integer.parseInt(user_id));
    }
}
