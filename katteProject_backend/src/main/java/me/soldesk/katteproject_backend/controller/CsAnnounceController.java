package me.soldesk.katteproject_backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import me.soldesk.katteproject_backend.service.CsAnnounceService;
import common.bean.cs.CsAnnounceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class CsAnnounceController {

    @Autowired
    private CsAnnounceService csAnnounceService;

    @PostMapping("/cs/post")
    @Operation(summary = "공지 등록", description = "공지를 등록 announce_category, announce_title, announce_content를 포함해야 함.")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<String> addAnnounce(@RequestBody @Valid CsAnnounceBean csAnnounceBean) {
        try {
            int addedRaw = csAnnounceService.addCsAnnounce(csAnnounceBean);
            if(addedRaw > 0) {
                return ResponseEntity.ok("문의가 등록되었습니다.");
            }else{
                return new ResponseEntity<>("문의 작성중 에러가 발생했습니다", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cs")
    @Operation(summary = "공지 조회", description = "공지 조회에 사용. announce_id가 없으면 공지 목록을 반환하고 입력하면 해당하는 공지의 내용이 출력")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<List<CsAnnounceBean>> getAnnounce(
            @RequestParam(required = false) Integer announce_id,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset)
    {
        try {
           return ResponseEntity.ok(csAnnounceService.getAnnounce(announce_id, count, offset));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cs/count")
    public Map<String, Integer>getAnnounceCount(){
        int count = csAnnounceService.getAnnounceCount();
        return Collections.singletonMap("count", count);
    }

    //1:1 문의 답변 상태별 리스트 조회
    @GetMapping("/cs/category")
    @Operation(summary = "공지사항을 카테고리별로 로드함", description = "announce_category를 반드시 포함해야하며, category는" +
            "ANNOUNCE, EVENT, ETC가 있음")
    public ResponseEntity<List<CsAnnounceBean>> getAnnounceByCategory(
            @RequestParam CsAnnounceBean.announce_category announce_category,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int offset
            ){
        try {
            List<CsAnnounceBean> announceList = csAnnounceService.getAnnounceByCategory(announce_category, count, offset);
            if(announceList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(csAnnounceService.getAnnounceByCategory(announce_category, count, offset));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/cs/edit")
    @Operation(summary = "공지 내용 수정", description = "announce_id, " +
            " announce_category, announce_title, announce_content를 받아서 수정함.")
    public ResponseEntity<String> updateAnnounce(@RequestBody CsAnnounceBean csAnnounceBean){
        try{
            csAnnounceService.updateAnnounce(csAnnounceBean);
            return ResponseEntity.ok("수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문의 수정 중 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/cs/del")
    @Operation(summary = "공지 삭제", description = "announce_id를 받아 해당 공지를 삭제")
    public ResponseEntity<String> deleteAnnounce(@RequestBody CsAnnounceBean csAnnounceBean){
        try{
            csAnnounceService.deleteCsAnnounce(csAnnounceBean);
            return new ResponseEntity<>("문의가 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문의 삭제 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
