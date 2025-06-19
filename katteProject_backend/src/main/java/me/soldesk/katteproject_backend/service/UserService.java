package me.soldesk.katteproject_backend.service;

import common.bean.product.ProductBrandLikeBean;
import common.bean.user.*;
import lombok.RequiredArgsConstructor;
import me.soldesk.katteproject_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public UserBean addUserInfo(UserBean joinUserBean) {
        userMapper.insertUser(joinUserBean);
        //회원 가입 동시에 페이먼트 테이블 함께 생성
        createDefaultUserPayment(Integer.toString(joinUserBean.getUser_id()));
        return joinUserBean;
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

    public void addUserAddress(UserAddressBean userAddressBean) {
        userMapper.addUserAddress(userAddressBean);
    }

    public List<UserAddressBean> getUserAddress(String user_id) {
        return userMapper.getUserAddresses(Integer.parseInt(user_id));
    }

    public List<UserAddressBean> getUserAddressDetail(String user_id, String id){
        return userMapper.getUserAddressDetail(Integer.parseInt(user_id), Integer.parseInt(id));
    }

    public UserAddressBean getUserMainAddress(String user_id) {
        return userMapper.getUserMainAddress(Integer.parseInt(user_id));
    }

/*    @Transactional
    public void updateMainAddress(String user_id, String address_id) {
        userMapper.resetMainAddress(Integer.parseInt(user_id));         // 모두 false
        userMapper.setMainAddress(Integer.parseInt(user_id), Integer.parseInt(address_id)); // 선택된 것만 true
    }*/
    @Transactional
    public void updateMainAddress(String user_id, String address_id) {
        System.out.println(">> updateMainAddress 실행");
        System.out.println("user_id = " + user_id + ", address_id = " + address_id);

        int uid = Integer.parseInt(user_id);
        int aid = Integer.parseInt(address_id);

        userMapper.resetMainAddress(uid);
        System.out.println("resetMainAddress 실행 완료");

        userMapper.setMainAddress(uid, aid);
        System.out.println("setMainAddress 실행 완료");
    }

    public boolean editUserAddress(UserAddressBean userAddressBean) {
        int updated = userMapper.editAddress(userAddressBean);
        return updated > 0;
    }

    public boolean deleteUserAddress(int user_id, int address_id) {
        int result = userMapper.deleteAddress(user_id, address_id);
        return result > 0;
    }



    @Transactional
    public int updateKatteMoney(UserKatteMoneyLogBean userKatteMoneyLogBean) {
        // 1. 로그 테이블에 기록
        userMapper.addKatteMoneyLog(userKatteMoneyLogBean); // INSERT INTO user_katte_money_log...

        // 2. 현재 잔액 가져오기
        int currentBalance = userMapper.selectKatteMoney(userKatteMoneyLogBean.getUser_id());
        int amount = userKatteMoneyLogBean.getChange_amount();

        // 3. 새 잔액 계산
        int updatedBalance = currentBalance;
        switch (userKatteMoneyLogBean.getReason()) {
            case CHARGE:
                updatedBalance += amount;
                break;
            case REFUNDED:
                updatedBalance -= amount;
                break;
            case USED:
                if (currentBalance < amount) {
                    throw new IllegalArgumentException("잔액 부족");
                }
                updatedBalance -= amount;
                break;
            default:
                throw new IllegalArgumentException("잘못된 reason: ");
        }

        // 4. user_payment 테이블 업데이트
        userMapper.updateKatteMoney(userKatteMoneyLogBean.getUser_id(), updatedBalance);

        return updatedBalance;
    }

    public void addKatteMoneyrefund(UserKatteMoneyRefundBean bean) {
        //1.환불 요청 기록
        userMapper.addKatteMoneyRefund(bean);

        //2.잔액 차감
        userMapper.subtractKatteMoney(bean.getUser_id(), bean.getAmount());

        //3.로그 기록
        UserKatteMoneyLogBean logBean = new UserKatteMoneyLogBean();
        logBean.setUser_id(bean.getUser_id());
        logBean.setChange_amount(-bean.getAmount()); // 마이너스로 기록
        logBean.setReason(UserKatteMoneyLogBean.reason.REFUNDED);
        userMapper.addKatteMoneyLog(logBean);

        int currentMoney = userMapper.selectKatteMoney(bean.getUser_id());
        if (bean.getAmount() > currentMoney) {
            throw new IllegalArgumentException("보유 금액 초과 환불 요청");
        }
    }

    public List<UserKatteMoneyRefundBean> getKatteMoneyRefund(int user_id) {
        return userMapper.getKatteMoneyRefunds(user_id);
    }

    public void updateKatteMoneyRefund(String status, int refund_id) {
        userMapper.updateKatteMoneyRefund(status, refund_id);
    }

    public void addUserTerm(int user_id, int term_code, boolean is_agreed) {
        userMapper.addUserTerm(user_id, term_code, is_agreed);
    }

    public Boolean getUserByNickname(String nickname) {
        return userMapper.getUserByNickname(nickname) != null;
    }

    public Boolean getUserByEmail(String email_id) {
        return userMapper.getUserByEmail(email_id) != null;
    }

    public List<ProductBrandLikeBean> getProductBrandLike(String user_id) {
        return userMapper.getProductBrandLike(Integer.parseInt(user_id));
    }

    public void updatePoint(int point, int user_id) {
        userMapper.updatePoint(user_id, point);
    }

}
