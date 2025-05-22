package me.soldesk.katteproject_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TESTUserApiController {

    @GetMapping ("/getData")
    public ResponseEntity<String> signup() {
        return ResponseEntity.ok("안녕하세요?");
    }
}
