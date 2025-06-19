package me.soldesk.katteproject_backend.controller;

import common.bean.admin.SoldoutProductViewBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import me.soldesk.katteproject_backend.service.AdminService;
import common.bean.user.UserBanBean;
import common.bean.user.UserRestrictionBean;
import common.bean.user.UserRestrictionUpdateBean;
import common.bean.admin.InspectionProductViewBean;
import common.bean.admin.UserAdminViewBean;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users/ban")
    @Operation(summary = "회원 정지 등록", description = "특정 회원을 로그인 정지 상태로 등록합니다.")
    @ApiResponse(responseCode = "200", description = "정지 등록 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public ResponseEntity<String> banUser(@RequestBody UserBanBean userBanBean) {
        adminService.registerUserBan(userBanBean);
        return ResponseEntity.ok(
                String.format("user_id=%d 의 유저 정지 등록 완료", userBanBean.getUser_id())
        );
    }

    @DeleteMapping("/users/ban")
    @Operation(summary = "회원 정지 해제", description = "특정 회원의 로그인 정지 상태를 해제합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> unbanUser(@RequestParam("user_id") int userId) {
        adminService.deleteUserBan(userId);
        return ResponseEntity.ok(
                String.format("user_id=%d 의 유저 정지 해제 완료", userId)
        );
    }

    @GetMapping("/users/ban")
    @Operation(summary = "회원 정지 여부 조회", description = "특정 회원이 정지 상태인지 여부를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Boolean> isUserBanned(@RequestParam("user_id") int userId) {
        boolean isBanned = adminService.isUserBanned(userId);
        return ResponseEntity.ok(isBanned);
    }

    @PostMapping("/users/restriction")
    @Operation(summary = "회원 제한 등록", description = "특정 회원에게 제한을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> restrictUser(@RequestBody UserRestrictionBean bean) {
        adminService.registerRestriction(bean);
        return ResponseEntity.ok(
                String.format("user_id=%d 의 유저 제한 등록 완료", bean.getUser_id())
        );
    }

    @DeleteMapping("/users/restriction")
    @Operation(summary = "회원 제한 해제", description = "특정 회원의 제한을 해제합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> unrestrictUser(
            @RequestParam("user_id") int userId,
            @RequestParam(value = "restriction_type", required = false) String restrictionType
    ) {
        if (restrictionType != null) {
            adminService.deleteSpecificRestriction(userId, restrictionType);
            return ResponseEntity.ok(String.format("user_id=%d의 %s 제한 해제 완료", userId, restrictionType));
        } else {
            adminService.deleteAllRestrictions(userId);
            return ResponseEntity.ok(String.format("user_id=%d 의 모든 제한 해제 완료", userId));
        }
    }

    @PatchMapping("/users/restriction")
    @Operation(summary = "회원 제한 수정", description = "특정 회원의 제한 기간(stop_days)을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> updateUserRestriction(
            @RequestParam("user_id") int userId,
            @RequestBody UserRestrictionUpdateBean bean) {

        adminService.updateRestriction(userId, bean.getStop_days());
        return ResponseEntity.ok(
                String.format("user_id=%d 의 유저 제한 기간이 %d일로 수정되었습니다.", userId, bean.getStop_days())
        );
    }

    @GetMapping("/users/restriction/check")
    @Operation(summary = "특정 제한 여부 확인", description = "특정 회원이 특정 유형의 제한을 받고 있는지 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Boolean> checkUserRestriction(
            @RequestParam("user_id") int userId,
            @RequestParam("restriction_type") String restrictionType) {

        boolean isRestricted = adminService.isUserRestricted(userId, restrictionType);
        return ResponseEntity.ok(isRestricted);
    }

    @GetMapping("/users/status")
    @Operation(summary = "회원 상태 조회", description = "회원의 정지 또는 제한 상태를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> getUserStatus(@RequestParam("user_id") int userId) {
        String statusMessage = adminService.getUserStatus(userId);
        return ResponseEntity.ok(statusMessage);
    }

    @GetMapping("/users")
    @Operation(summary = "회원 목록 조회", description = "전체 회원 목록 또는 필터 조건에 맞는 회원 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<UserAdminViewBean>> getUserList(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String filter
    ) {
        return ResponseEntity.ok(adminService.getUserList(offset, size, filter));
    }

    @GetMapping("/users/count")
    @Operation(summary = "전체 회원 수 조회", description = "회원 목록의 전체 갯수를 정수로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Integer> getUserTotalCount() {
        return ResponseEntity.ok(adminService.getUserTotalCount());
    }

    @GetMapping("/users/restrictions")
    @Operation(summary = "유저 제한 목록 조회", description = "해당 유저의 현재 유효한 제한 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    public ResponseEntity<List<UserRestrictionBean>> getUserRestrictions(@RequestParam("user_id") int userId) {
        System.out.println("💡 [백엔드] user_id 파라미터 수신: " + userId);
        List<UserRestrictionBean> restrictions = adminService.getValidRestrictions(userId);
        return ResponseEntity.ok(restrictions);
    }

    @GetMapping("/users/search")
    @Operation(summary = "유저 검색", description = "이메일 ID , 닉네임 , 유저 ID로 유저 검색")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<UserAdminViewBean>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(adminService.searchUsers(keyword, offset, size));
    }

    @GetMapping("/users/search/count")
    @Operation(summary = "유저 검색 결과 수 조회", description = "키워드에 해당하는 유저 총 수 반환")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Integer> searchUsersCount(@RequestParam String keyword) {
        return ResponseEntity.ok(adminService.searchUsersCount(keyword));
    }

    @GetMapping("/user")
    @Operation(summary = "단건 유저 조회", description = "user_id로 단건 유저 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public UserAdminViewBean getUserById(@RequestParam("user_id") int userId) {
        return adminService.findUserById(userId);
    }

    @PatchMapping("/inspection/complete")
    @Operation(summary = "검수 완료 처리", description = "검수를 완료하고 상태를 on_sale로 변경")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> completeInspection(@RequestParam("check_result_id") int checkResultId) {
        adminService.completeInspection(checkResultId);
        return ResponseEntity.ok(String.format("검수 결과 ID %d → 완료 처리됨", checkResultId));
    }

    @PatchMapping("/inspection/fail")
    @Operation(summary = "검수 실패 처리", description = "검수가 실패되어 상태를 inspection_fail로 변경")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> failInspection(@RequestParam("check_result_id") int checkResultId) {
        adminService.failInspection(checkResultId);
        return ResponseEntity.ok(String.format("검수 결과 ID %d → 반송 처리됨", checkResultId));
    }

    @GetMapping("/inspection")
    @Operation(summary = "검수 목록 조회", description = "inspection_product_view에서 페이징된 검수 항목 반환")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<InspectionProductViewBean>> getInspectionProductViewList(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getInspectionProductViewList(offset, size));
    }

    @GetMapping("/inspection/count")
    @Operation(summary = "전체 검수 항목 수 조회", description = "검수 목록의 전체 갯수를 정수로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Integer> getInspectionCount() {
        return ResponseEntity.ok(adminService.getInspectionTotalCount());
    }

    @DeleteMapping("inspection/delete")
    @Operation(summary = "검수 실패/만료된 항목 삭제", description = "검수 실패 or 만료 상태이며 3일 경과한 항목을 일괄 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @ApiResponse(responseCode = "400", description = "요청 실패")
    public ResponseEntity<String> deleteExpiredOrFailedInspections() {
        int count = adminService.deleteExpiredOrFailedInspections();
        return ResponseEntity.ok(String.format("총 %d개의 검수/판매 항목이 삭제되었습니다.", count));
    }

    //옥션 id를 통한 판매 항목 삭제
    @DeleteMapping("/inspection/delete_one")
    @Operation(summary = "단일 판매 항목 삭제", description = "특정 auction_id에 해당하는 판매 항목을 삭제합니다 (조건: EXPIRED + 3일 경과).")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @ApiResponse(responseCode = "400", description = "요청 실패")
    public ResponseEntity<String> deleteSingleExpiredSale(@RequestParam("auction_id") int auctionId) {
        int deleted = adminService.deleteExpiredSaleByAuctionId(auctionId);
        if (deleted > 0) {
            return ResponseEntity.ok(String.format("auction_id=%d → 판매 항목 삭제 완료", auctionId));
        } else {
            return ResponseEntity.badRequest().body(String.format("auction_id=%d → 삭제 조건 불충족 (EXPIRED + 3일 경과 필요)", auctionId));
        }
    }

    @PatchMapping("/inspection/expire_now")
    @Operation(summary = "만료 항목 즉시 삭제 예약", description = "EXPIRED 상태의 경매를 삭제 가능 상태로 즉시 조작합니다.")
    @ApiResponse(responseCode = "200", description = "예약 성공")
    @ApiResponse(responseCode = "400", description = "요청 실패")
    public ResponseEntity<String> markExpiredAuctionForDelete(@RequestParam("auction_id") int auctionId) {
        adminService.markAuctionForImmediateDeletion(auctionId);
        return ResponseEntity.ok(String.format("auction_id=%d → 즉시 삭제 대상으로 설정됨", auctionId));
    }


}
