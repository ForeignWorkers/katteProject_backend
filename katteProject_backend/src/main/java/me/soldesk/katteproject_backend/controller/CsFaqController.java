package me.soldesk.katteproject_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import me.soldesk.katteproject_backend.service.CsFaqService;
import common.bean.cs.CsFaqBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CsFaqController {

    @Autowired
    CsFaqService csFaqService;

    @PostMapping("/cs/faq/post")
    @Operation(summary = "자주 묻는 질문을 직접 작성해서 등록", description = "자주 묻는 질문 등록 faq_category, faq_title, faq_content를 포함해야 함.")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<String> addFaq(@RequestBody @Valid CsFaqBean csFaqBean) {
        try {
            int addedRaw = csFaqService.addFaq(csFaqBean);
            if(addedRaw > 0) {
                return ResponseEntity.ok("자주 묻는 질문이 등록되었습니다.");
            }else{
                return new ResponseEntity<>("자주 묻는 질문 작성중 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("자주 묻는 질문을 등록중 서버 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cs/faq")
    @Operation(summary = "자주 묻는 질문 조회", description = "자주 묻는 질문 조회에 사용. faq_id가 없으면 자주 묻는 질문 목록을 반환하고 입력하면" +
            "해당하는 자주 묻는 질문의 내용이 출력")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<List<CsFaqBean>> getFaqs(
            @RequestParam(required = false) Integer faq_id,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset
            )
    {
        try {
           csFaqService.getFaqs(faq_id, count, offset);
           return ResponseEntity.ok(csFaqService.getFaqs(faq_id, count, offset));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cs/faq/category")
    @Operation(summary = "카테고리별로 자주 묻는 질문 조회", description = "자주 묻는 질문 카테고리별 조회에 사용. faq_category를 필수로 하며 " +
            "해당하는 카테고리로 분류해줌.")
    public ResponseEntity<List<CsFaqBean>> getFaqCategory(
            @RequestParam CsFaqBean.faq_category faq_category,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset
            ){
        try {
            List<CsFaqBean> faqList = csFaqService.getFaqByCategory(faq_category, count, offset);
            if(faqList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(faqList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/cs/faq/edit")
    @Operation(summary = "자주 묻는 질문 수정", description = "faq_id, " +
            "faq_category, faq_title, faq_content를 받아서 수정함.")
    public ResponseEntity<String> updateFaq(@RequestBody CsFaqBean csFaqBean){
        try{
            csFaqService.updateFaq(csFaqBean);
            return ResponseEntity.ok("수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문의 수정 중 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/cs/faq/del")
    @Operation(summary = "공지 삭제", description = "announce_id를 받아 해당 공지를 삭제")
    public ResponseEntity<String> deleteFaq(@RequestBody CsFaqBean csFaqBean){
        try{
            csFaqService.deleteFaq(csFaqBean);
            return new ResponseEntity<>("문의가 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문의 삭제 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
