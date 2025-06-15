package me.soldesk.katteproject_backend.controller;

import common.bean.cs.CsInquireCustomerBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import me.soldesk.katteproject_backend.service.CsReplyService;
import common.bean.cs.CsReplyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CsReplyController {

    @Autowired
    private CsReplyService csReplyService;

    public CsReplyController(CsReplyService csReplyService) {
        this.csReplyService = csReplyService;
    }
    //1:1 문의 답변 작성
    @PostMapping("/cs/reply/post")
    @Operation(summary = "문의 답변을 직접 작성해서 등록", description = "inquire_id, reply_title, reply_content를 포함해야 함.")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<String> addReply(@RequestBody @Valid CsReplyBean csReplyBean){
        try {
            int addedRaw = csReplyService.addReply(csReplyBean);
            if(addedRaw > 0){
                return ResponseEntity.ok("문의 답변이 등록되었습니다.");
            }else{
                return new ResponseEntity<>("문의 답변 작성중 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문의 답변 등록중 서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //1:1 문의 답변 조회
    @GetMapping("/cs/reply")
    @Operation(summary = "문의 답변을 조회함", description = "공백으로 보내면 리스트를, reply_id와 inquire_id를 입력하면 상세 내역을 로드함.")
    public ResponseEntity<List<CsReplyBean>> getCsReply(
            @RequestParam(required = false) Integer reply_id,
            @RequestParam(required = false) Integer inquire_id,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset
            ){
        try {
                csReplyService.getCsReply(reply_id, inquire_id, count, offset);
                return ResponseEntity.ok(csReplyService.getCsReply(reply_id, inquire_id, count, offset));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //1:1 문의 상태별 리스트 조회
    @GetMapping("/cs/reply/status")
    @Operation(summary = "문의를 진행 상황별로 로드함", description = "inquire_status를 포함해야 함.PENDING, ONGOING, COMPLETE가 있음")
    public ResponseEntity<List<CsInquireCustomerBean>> getCsCategory(
            @RequestParam CsInquireCustomerBean.Inquire_Status inquire_status,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset
            ){
        try {
            List<CsInquireCustomerBean> inquireList = csReplyService.getCsInquireByStatusAdmin(inquire_status, count, offset);
            if(inquireList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(csReplyService.getCsInquireByStatusAdmin(inquire_status, count, offset));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //1:1 문의 답변 수정
    @PatchMapping("cs/reply/edit")
    @Operation(summary = "문의 답변 내역 수정", description = "reply_id, reply_title, reply_content를 받아서 수정함.")
    public ResponseEntity<String> updateReply(@RequestBody CsReplyBean csReplyBean){
        try{
            csReplyService.updateReply(csReplyBean);
            return ResponseEntity.ok("수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문의 수정 중 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //1:1 문의 답변 삭제
    @DeleteMapping("/cs/reply/del")
    @Operation(summary = "문의 답변을 삭제", description = "reply_id를 포함해야 함.")
    public ResponseEntity<String> deleteReply(@RequestBody @Valid CsReplyBean csReplyBean){
        try {
            csReplyService.deleteReply(csReplyBean);
            return new ResponseEntity<>("문의 답변이 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문의 답변 삭제 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
