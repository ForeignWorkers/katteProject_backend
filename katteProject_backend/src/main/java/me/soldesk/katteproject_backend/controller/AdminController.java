package me.soldesk.katteproject_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import me.soldesk.katteproject_backend.service.AdminService;
import common.bean.user.UserBanBean;
import common.bean.user.UserRestrictionBean;
import common.bean.user.UserRestrictionUpdateBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<String> unrestrictUser(@RequestParam("user_id") int userId) {
        adminService.deleteRestriction(userId);
        return ResponseEntity.ok(
                String.format("user_id=%d 의 유저 제한 해제 완료", userId)
        );
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

    @GetMapping("/users/status")
    @Operation(summary = "회원 상태 조회", description = "회원의 정지 또는 제한 상태를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> getUserStatus(@RequestParam("user_id") int userId) {
        String statusMessage = adminService.getUserStatus(userId);
        return ResponseEntity.ok(statusMessage);
    }

}
