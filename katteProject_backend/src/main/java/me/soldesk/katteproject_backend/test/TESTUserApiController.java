//package me.soldesk.katteproject_backend.test;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//@RestController
//public class TESTUserApiController {
//
//    @GetMapping ("/getData")
//    public ResponseEntity<String> signup() {
//        return ResponseEntity.ok("안녕하세요?");
//    }
//
//    @GetMapping("/resource")
//    public ResponseEntity<byte[]> resource() throws IOException {
//        InputStream in = getClass().getResourceAsStream("/static/images/product_1.png");
//
//        if (in == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        byte[] imageBytes = in.readAllBytes();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_PNG); // 이미지 타입 지정
//        headers.setContentLength(imageBytes.length);
//
//        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//    }
//}