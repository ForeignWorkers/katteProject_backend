package me.soldesk.katteproject_backend.controller;

import common.bean.UserBean;
import me.soldesk.katteproject_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseEntity<String> addUserinfo(@RequestBody @Valid UserBean userBean, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().toString());
        }

        userService.addUserInfo(userBean);
        return ResponseEntity.ok("유저 등록이 되었습니다 !");
    }
}
