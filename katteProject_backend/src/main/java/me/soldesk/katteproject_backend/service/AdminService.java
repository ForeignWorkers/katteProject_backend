package me.soldesk.katteproject_backend.service;

import lombok.RequiredArgsConstructor;
import me.soldesk.katteproject_backend.mapper.AdminMapper;
import common.bean.user.UserBanBean;
import common.bean.user.UserRestrictionBean;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;

    //정지 등록
    public void registerUserBan(UserBanBean userBanBean) {
        //예외 상황 없으면 현재 시간으로 등록 처리 (혹은 요청받은 값 그대로 사용)
        if (userBanBean.getBan_start() == null) {
            userBanBean.setBan_start(new Date());
        }

        adminMapper.insertUserBan(userBanBean);
    }

    //정지 등록 해제
    public void deleteUserBan(int userId) {
        adminMapper.deleteUserBan(userId);
    }

    //제한 등록
    public void registerRestriction(UserRestrictionBean bean) {
        Date now = new Date();
        bean.setStart_date(now);
        // stop_days 기준으로 end_date 계산
        long endMillis = now.getTime() + ((long) bean.getStop_days()) * 24 * 60 * 60 * 1000;
        bean.setEnd_date(new Date(endMillis));

        adminMapper.insertUserRestriction(bean);
    }

    //제한 해제
    public void deleteRestriction(int user_id) {
        adminMapper.deleteUserRestriction(user_id);
    }

    //제한 수정
    public void updateRestriction(int user_id, int stop_days) {
        adminMapper.updateUserRestriction(user_id, stop_days);
    }

    //유저 상태 조회
    public String getUserStatus(int userId) {
        //정지 여부 먼저 확인
        UserBanBean ban = adminMapper.findBanByUserId(userId);
        if (ban != null) {
            return String.format("user_id=%d 는 정지 상태입니다.", userId);
        }
        //제한 여부 확인
        UserRestrictionBean restriction = adminMapper.findValidRestrictionByUserId(userId);
        if (restriction != null) {
            return String.format("user_id=%d 는 %s 제한 상태 (%d일)",
                    userId,
                    restriction.getRestriction_type(),
                    restriction.getStop_days());
        }
        //아무 제한 없음
        return String.format("user_id=%d 는 정상 상태 또는 존재하지 않습니다.", userId);
    }
}
