package me.soldesk.katteproject_backend.controller;


import common.bean.cs.CsInquireCustomerBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import me.soldesk.katteproject_backend.service.CsInquireCustomerService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CsInquireController {

    @Autowired
    CsInquireCustomerService csInquireCustomerService;

    @PostMapping("/cs/inquiry/post")
    @Operation(summary = "문의 등록", description = "문의를 등록하는 역할을 하며 user_id, inquire_category, inquire_title, inquire_content를 꼭 포함해야 함.")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<String> addInquire(@RequestBody @Valid CsInquireCustomerBean csInquireCustomerBean) {
       try {
           int addedRaw = csInquireCustomerService.addCsInquireCustomer(csInquireCustomerBean);
           if(addedRaw > 0) {
               return ResponseEntity.ok("문의가 등록되었습니다.");
           }else{
               return new ResponseEntity<>("문의 작성중 에러가 발생했습니다", HttpStatus.BAD_REQUEST);
           }
       } catch (Exception e) {
           e.printStackTrace();
           return new ResponseEntity<>("문의 등록 중 서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/cs/inquiry")
    @Operation(summary = "문의 내역 조회 및 상세 페이지", description = "user_id로 문의 내역을 가져오거나, " +
            "user_id와 inquire_id로 상세 내역을 가져옴")
    @ApiResponse(responseCode = "200", description = "성공 - 문의 내역 또는 상세 내역 반환")
    @ApiResponse(responseCode = "500", description = "서버 에러")
    public ResponseEntity<List<CsInquireCustomerBean>> getInquiry(
            @RequestParam int user_id,
            @RequestParam(required = false) Integer inquire_id,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset
    ) {
        try {
            csInquireCustomerService.getCsInquire(user_id, inquire_id, count, offset);
            return ResponseEntity.ok(csInquireCustomerService.getCsInquire(user_id, inquire_id, count, offset));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //1:1 문의 답변 상태별 리스트 조회
    @GetMapping("/cs/inquiry/category")
    public ResponseEntity<List<CsInquireCustomerBean>> getCsCategory(
            @RequestParam int user_id,
            @RequestParam CsInquireCustomerBean.inquire_status inquire_status,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset
            ){
        try {
            List<CsInquireCustomerBean> inquireList = csInquireCustomerService.getCsInquireByCategoryCustomer(user_id, inquire_status, count, offset);
            if(inquireList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(csInquireCustomerService.getCsInquireByCategoryCustomer(user_id, inquire_status, count, offset));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("cs/inquiry/edit")
    @Operation(summary = "문의 내역 수정", description = "user_id, inquire_id, " +
            " inquire_category, inquire_title, inquire_content를 받아서 수정함.")
    public ResponseEntity<String> updateInquire(@RequestBody CsInquireCustomerBean csInquireCustomerBean){
        try{
            csInquireCustomerService.updateCsInquireCustomer(csInquireCustomerBean);
            return ResponseEntity.ok("수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문의 수정 중 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("cs/inquiry/del")
    @Operation(summary = "문의 삭제", description = "userId와 inquireId를 받아 해당 문의를 삭제")
    public ResponseEntity<String> deleteInquire(@RequestBody CsInquireCustomerBean csInquireCustomerBean){
        try{
            csInquireCustomerService.deleteCsInquireCustomer(csInquireCustomerBean);
            return new ResponseEntity<>("문의가 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문의 삭제 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
