package me.soldesk.katteproject_backend.service;

import lombok.RequiredArgsConstructor;
import me.soldesk.katteproject_backend.mapper.AdminMapper;
import common.bean.user.UserBanBean;
import common.bean.user.UserRestrictionBean;
import common.bean.admin.InspectionProductViewBean;
import common.bean.admin.UserAdminViewBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        //1. 정지 여부 확인
        UserBanBean ban = adminMapper.findBanByUserId(userId);
        if (ban != null) {
            return "계정 정지";
        }

        //2. 제한 여부 확인 (여러 개 가능)
        List<UserRestrictionBean> restrictions = adminMapper.findAllValidRestrictionsByUserId(userId);
        if (restrictions != null && !restrictions.isEmpty()) {
            List<String> statusParts = new ArrayList<>();
            for (UserRestrictionBean r : restrictions) {
                String typeName = switch (r.getRestriction_type()) {
                    case "comment" -> "댓글 작성 제한";
                    case "style" -> "게시글 작성 제한";
                    case "sale" -> "판매 제한";
                    default -> "알 수 없음";
                };
                statusParts.add(String.format("%s %d일", typeName, r.getStop_days()));
            }
            return String.join(", ", statusParts);
        }

        //3. 아무 제한 없음
        return "정상";
    }

    //회원 리스트 조회
    public List<UserAdminViewBean> getUserList(int offset, int size, String filter) {
        return adminMapper.findUserList(offset, size, filter);
    }

    //전체 회원 수
    public int getUserTotalCount() {
        return adminMapper.getUserTotalCount();
    }

    //회원 검색
    public List<UserAdminViewBean> searchUsers(String keyword) {
        return adminMapper.searchUsersByKeyword(keyword);
    }

    //회원 단건
    public UserAdminViewBean findUserById(int userId) {
        return adminMapper.findUserById(userId);
    }

    //검수 상태 변경(검수 성공)
    public void completeInspection(int checkResultId) {
        adminMapper.markInspectionComplete(checkResultId);
    }

    //검수 상태 변경(조건 미달)
    public void failInspection(int checkResultId) {
        adminMapper.markInspectionFail(checkResultId);
    }

    //검수 리스트 조회
    public List<InspectionProductViewBean> getInspectionProductViewList(int offset, int size) {
        return adminMapper.getInspectionProductViewList(offset, size);
    }

    //전체 검수 건수
    public int getInspectionTotalCount() {
        return adminMapper.getInspectionTotalCount();
    }

    //검수 데이터 삭제
    @Transactional
    public int deleteExpiredOrFailedInspections() {
        List<Integer> ids = adminMapper.getDeletableCheckResultIds();
        for (int id : ids) {
            adminMapper.deleteCheckResultById(id);
            adminMapper.deletePerSaleByCheckResultId(id);
        }
        return ids.size();
    }

}
