package me.soldesk.katteproject_backend.controller;

import common.bean.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.soldesk.katteproject_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/user")
    //API Docs
    @Operation(summary = "유저 등록", description = "유저를 새롭게 등록합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<UserBean> addUserinfo(@RequestBody @Valid UserBean userBean, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }

        UserBean signedUser = userService.addUserInfo(userBean);
        return ResponseEntity.ok(signedUser);
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
    //API Docs
    @Operation(summary = "유저 비밀번호 수정", description = "쿼리에는 유저 아이디, 바디에는 password : 패스워드")
    public ResponseEntity<String> updateUserInfoPassword(@RequestParam String user_id, @RequestBody Map<String, String> password) {
        userService.updateUserInfoPassword(user_id, password.get("password"));
        return ResponseEntity.ok("비밀번호가 업데이트되었습니다.");
    }

    @PatchMapping("/user/phone")
    //API Docs
    @Operation(summary = "유저 전화번호 수정", description = "쿼리에는 유저 아이디, 바디에는 phone_number : 번호")
    public ResponseEntity<String> updateUserInfoPhone(@RequestParam String user_id, @RequestBody Map<String, String> phone) {
        userService.updateUserInfoPhone(user_id, phone.get("phone_number"));
        return ResponseEntity.ok("전화번호가 업데이트되었습니다.");
    }

    @PatchMapping("/user/nickname")
    //API Docs
    @Operation(summary = "유저 닉네임 수정", description = "쿼리에는 유저 아이디, 바디에는 nickname : 닉네임")
    public ResponseEntity<String> updateUserInfoNickname(@RequestParam String user_id, @RequestBody Map<String, String> nickname) {
        userService.updateUserInfoNickname(user_id, nickname.get("nickname"));
        return ResponseEntity.ok("닉네임이 업데이트되었습니다.");
    }

    @PatchMapping("/user/introduce")
    //API Docs
    @Operation(summary = "유저 자기소개 수정", description = "쿼리에는 유저 아이디, 바디에는 introduce : 내용")
    public ResponseEntity<String> updateUserInfoIntroduce(@RequestParam String user_id, @RequestBody Map<String, String> introduce) {
        userService.updateUserInfoIntroduce(user_id, introduce.get("introduce"));
        return ResponseEntity.ok("자기소개가 업데이트 되었습니다.");
    }

    @PatchMapping("/user/profile_url")
    //API Docs
    @Operation(summary = "유저 프로필 수정", description = "쿼리에는 유저 아이디, 바디에는  profile_url : 프로필 URL")
    public ResponseEntity<String> updateUserInfoProfileUrl(@RequestParam String user_id, @RequestBody Map<String, String> profile_url) {
        userService.updateUserInfoProfile(user_id, profile_url.get("profile_url"));
        return ResponseEntity.ok("프로필 사진이 업데이트 되었습니다.");
    }

    @DeleteMapping("/user")
    //API Docs
    @Operation(summary = "유저 삭제", description = "쿼리에는 유저 아이디")
    public ResponseEntity<String> deleteUser(@RequestParam String user_id) {
        userService.deleteUserInfo(user_id);
        return ResponseEntity.ok(user_id + "유저가 삭제 되었습니다.");
    }

    @PostMapping("/user/login")
    //API Docs
    @Operation(summary = "로그인 가능 여부 확인", description = "바디에 유저 이메일, 비밀번호 넣기")
    public ResponseEntity<Boolean> login(@RequestBody Map<String,String> loginData) {
        String email_id = loginData.get("email_id");
        String rawPassword = loginData.get("password");

        UserBean user = userService.getUserInfo(null,email_id);

        if (user == null) return ResponseEntity.ok(false);

        // 서버가 가지고 있는 암호화된 패스워드와 비교
        boolean result =  passwordEncoder.matches(rawPassword, user.getPassword());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/payment")
    //API Docs
    @Operation(summary = "페이먼트 정보 조회", description = "쿼리에 유저 id 넣기")
    public ResponseEntity<UserPaymentBean> payment(@RequestParam String user_id) {
        UserPaymentBean getData = userService.getUserPayment(user_id);
        return ResponseEntity.ok(getData);
    }

    @PostMapping("/user/address")
    //API Docs
    @Operation(summary = "유저 주소 등록", description = "유저 주소 등록하기")
    public ResponseEntity<String> addAddress(@RequestBody UserAddressBean userAddressBean) {
        userService.addUserAddress(userAddressBean);
        return ResponseEntity.ok("주소 등록이 완료 되었습니다.");
    }

    @GetMapping("/user/address")
    //API Docs
    @Operation(summary = "유저가 등록한 주소 조회", description = "유저의 주소들 리스트 반환")
    public ResponseEntity<List<UserAddressBean>> getUserAddress(@RequestParam String user_id) {
        return ResponseEntity.ok(userService.getUserAddress(user_id));
    }

    @GetMapping("/user/address/detail")
    @Operation(summary = "유저 주소 상세 조회", description = "유저의 상세 주소 리스트 반환")
    public ResponseEntity<List<UserAddressBean>> getUserAddressDetail(@RequestParam String user_id,
                                                                @RequestParam String id) {
        return ResponseEntity.ok(userService.getUserAddressDetail(user_id, id));
    }

    @GetMapping("/user/address/main")
    //API Docs
    @Operation(summary = "유저의 메인 주소 조회", description = "유저의 메인 주소 반환")
    public ResponseEntity<UserAddressBean> getAddress(@RequestParam String user_id) {
        return ResponseEntity.ok(userService.getUserMainAddress(user_id));
    }

    @PatchMapping("/user/address/main")
    ///API Docs
    @Operation(summary = "유저의 메인 변경", description = "유저의 메인 주소 반환")
    public ResponseEntity<String> setMainAddress(@RequestParam String user_id,
                                                 @RequestParam String address_id) {
        userService.updateMainAddress(address_id, user_id);
        return ResponseEntity.ok("main 주소가 바뀌었습니다.");
    }

    @PatchMapping("/user/address/edit")
    @Operation(summary = "유저의 주소 수정", description = "body로 id, user_id, name, phone_number, address_line01/02, post_num 다 필요함")
    public ResponseEntity<String> updateUserAddress(@RequestBody UserAddressBean userAddressBean) {
        boolean result = userService.editUserAddress(userAddressBean);
        if (result) {
            return ResponseEntity.ok("주소를 수정하였습니다.");
        } else {
            return ResponseEntity.badRequest().body("주소 수정에 실패했습니다.");
        }
    }

    @DeleteMapping("/user/address/del")
    @Operation(summary = "유저 주소 삭제")
    public ResponseEntity<String> deleteAddress(
            @RequestParam int user_id,
            @RequestParam int address_id) {

        boolean deleted = userService.deleteUserAddress(user_id, address_id);

        if (deleted) {
            return ResponseEntity.ok("주소가 정상적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("주소를 삭제하지 못했습니다.");
        }
    }

    @PatchMapping("/user/katte")
    ///API Docs
    @Operation(summary = "katte 머니 변경 요청", description = "캇테 머니 변경")
    public ResponseEntity<String> updatekatteMoney(@RequestBody UserKatteMoneyLogBean katteMoneyLogBean) {
        int currentMoney = userService.updateKatteMoney(katteMoneyLogBean);
        return ResponseEntity.ok("요청이 완료 되었습니다. 현재 머니 : " + currentMoney);
    }

    @PostMapping("/user/katte/refund")
    ///API Docs
    @Operation(summary = "katte 머니 환불 요청", description = "환불 요청 서 생성")
    public ResponseEntity<String> addKatteMoneyRefund(@RequestBody UserKatteMoneyRefundBean userKatteMoneyRefundBean) {
        userService.addKatteMoneyrefund(userKatteMoneyRefundBean);
        return ResponseEntity.ok("환불 요청서 생성 완료");
    }

    @GetMapping("/user/katte/refund")
    @Operation(summary = "특정 유저가 요청한 환불서 조회", description = "특정 유저의 요청서 리스트로 반환")
    public ResponseEntity<List<UserKatteMoneyRefundBean>> getKatteMoneyRefund(@RequestParam String user_id) {
        return ResponseEntity.ok(userService.getKatteMoneyRefund(Integer.parseInt(user_id)));
    }

    @PatchMapping("/user/katte/refund")
    ///API Docs
    @Operation(summary = "katte 머니 환불 상태 업데이트", description = "환불서의 상태 업데이트")
    public ResponseEntity<String> updateKatteMoneyRefund(@RequestParam UserKatteMoneyRefundBean.status status,
                                                         @RequestParam int refund_id){
        userService.updateKatteMoneyRefund(status, refund_id);
        return ResponseEntity.ok(String.format("%d의 환불서의 상태가 %s로 업데이트 되었습니다.", refund_id, status));
    }

    @PostMapping("/user/terms")
    public ResponseEntity<String> addUserTerms(@RequestBody Map<String,String> termData) {
        String user_id = termData.get("user_id");
        int term_code = Integer.valueOf(termData.get("term_code"));
        boolean is_agreed = Boolean.parseBoolean(termData.get("is_agreed"));

        userService.addUserTerm(Integer.parseInt(user_id),term_code,is_agreed);
        return ResponseEntity.ok("유저의 약관 동의 완료");
    }

    @GetMapping("/user/nickname")
    public ResponseEntity<Boolean> isExistUserNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.getUserByNickname(nickname));
    }

    @GetMapping("/user/duplicate")
    public ResponseEntity<Boolean> isExistUserEmail(@RequestParam String email_id) {
        return ResponseEntity.ok(userService.getUserByEmail(email_id));
    }
}