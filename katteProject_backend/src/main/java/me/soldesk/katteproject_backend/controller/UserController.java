package me.soldesk.katteproject_backend.controller;

import common.bean.user.UserBean;
import common.bean.user.UserPaymentBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.soldesk.katteproject_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    //API Docs
    @Operation(summary = "유저 등록", description = "유저를 새롭게 등록합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> addUserinfo(@RequestBody @Valid UserBean userBean, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().toString());
        }

        userService.addUserInfo(userBean);
        return ResponseEntity.ok("유저 등록이 되었습니다 !");
    }

    @GetMapping("/user")
    //API Docs
    @Operation(summary = "유저 조회", description = "유저를 조회합니다. 2개의 쿼리는 필수! user_id를 null로 보내면 email_id로 찾습니다.")
    @ApiResponse(responseCode = "200", description = "UserBean 리턴")
    public ResponseEntity<UserBean> getUserinfo(@RequestParam String user_id, @RequestParam String email_id) {
        return ResponseEntity.ok(userService.getUserInfo(user_id, email_id));
    }


    @PatchMapping("/user/email_id")
    //API Docs
    @Operation(summary = "유저 이메일 수정", description = "쿼리에는 유저 아이디, 바디에는 email_id : 이메일")
    public ResponseEntity<String> updateUserInfoEmail(@RequestParam String user_id, @RequestBody Map<String, String> email_id) {
        userService.updateUserInfoEmail(user_id, email_id.get("email_id"));
        return ResponseEntity.ok("이메일이 업데이트되었습니다.");
    }

    @PatchMapping("/user/password")
    //API Doce
    @Operation(summary = "유저 비밀번호 수정", description = "쿼리에는 유저 아이디, 바디에는 password : 패스워드")
    public ResponseEntity<String> updateUserInfoPassword(@RequestParam String user_id, @RequestBody Map<String, String> password) {
        userService.updateUserInfoPassword(user_id, password.get("password"));
        return ResponseEntity.ok("비밀번호가 업데이트되었습니다.");
    }

    @PatchMapping("/user/phone")
    //API Doce
    @Operation(summary = "유저 전화번호 수정", description = "쿼리에는 유저 아이디, 바디에는 phone_number : 번호")
    public ResponseEntity<String> updateUserInfoPhone(@RequestParam String user_id, @RequestBody Map<String, String> phone) {
        userService.updateUserInfoPhone(user_id, phone.get("phone_number"));
        return ResponseEntity.ok("전화번호가 업데이트되었습니다.");
    }

    @PatchMapping("/user/nickname")
    //API Doce
    @Operation(summary = "유저 닉네임 수정", description = "쿼리에는 유저 아이디, 바디에는 nickname : 닉네임")
    public ResponseEntity<String> updateUserInfoNickname(@RequestParam String user_id, @RequestBody Map<String, String> nickname) {
        userService.updateUserInfoNickname(user_id, nickname.get("nickname"));
        return ResponseEntity.ok("닉네임이 업데이트되었습니다.");
    }

    @PatchMapping("/user/introduce")
    //API Doce
    @Operation(summary = "유저 자기소개 수정", description = "쿼리에는 유저 아이디, 바디에는 introduce : 내용")
    public ResponseEntity<String> updateUserInfoIntroduce(@RequestParam String user_id, @RequestBody Map<String, String> introduce) {
        userService.updateUserInfoIntroduce(user_id, introduce.get("introduce"));
        return ResponseEntity.ok("자기소개가 업데이트 되었습니다.");
    }

    @PatchMapping("/user/profile_url")
    //API Doce
    @Operation(summary = "유저 프로필 수정", description = "쿼리에는 유저 아이디, 바디에는  profile_url : 프로필 URL")
    public ResponseEntity<String> updateUserInfoProfileUrl(@RequestParam String user_id, @RequestBody Map<String, String> profile_url) {
        userService.updateUserInfoProfile(user_id, profile_url.get("profile_url"));
        return ResponseEntity.ok("프로필 사진이 업데이트 되었습니다.");
    }

    @DeleteMapping("/user")
    //API Doce
    @Operation(summary = "유저 삭제", description = "쿼리에는 유저 아이디")
    public ResponseEntity<String> deleteUser(@RequestParam String user_id) {
        userService.deleteUserInfo(user_id);
        return ResponseEntity.ok(user_id + "유저가 삭제 되었습니다.");
    }

    @GetMapping("/user/login")
    //API Doce
    @Operation(summary = "로그인 가능 여부 확인", description = "바디에 유저 이메일, 비밀번호 넣기")
    public ResponseEntity<Boolean> login(@RequestBody Map<String,String> loginData) {
        String email_id = loginData.get("email_id");
        String password = loginData.get("password");

        boolean result = userService.existsUserInfo(email_id, password);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/payment")
    //API Doce
    @Operation(summary = "페이먼트 정보 조회", description = "쿼리에 유저 id 넣기")
    public ResponseEntity<UserPaymentBean> payment(@RequestParam String user_id) {
        UserPaymentBean getData = userService.getUserPayment(user_id);
        return ResponseEntity.ok(getData);
    }
}