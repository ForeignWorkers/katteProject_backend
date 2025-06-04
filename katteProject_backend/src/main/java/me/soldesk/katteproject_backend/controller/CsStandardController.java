package me.soldesk.katteproject_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import me.soldesk.katteproject_backend.service.CsStandardService;
import common.bean.cs.CsStandardBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CsStandardController {

    @Autowired
    CsStandardService csStandardService;

    @PostMapping("/cs/standard/post")
    @Operation(summary = "검수 기준을 직접 작성해서 등록", description = "검수 기준 등록 standard_category, standard_content를 포함해야 함.")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<String> addFaq(@RequestBody @Valid CsStandardBean csStandardBean) {
        try {
            int addedRaw = csStandardService.addStandard(csStandardBean);
            if(addedRaw > 0) {
                csStandardService.addStandard(csStandardBean);
                return ResponseEntity.ok("검수 기준이 등록되었습니다.");
            }else{
                return new ResponseEntity<>("검수 기준 작성중 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("검수 기준을 등록중 서버 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cs/standard")
    @Operation(summary = "검수 기준 조회", description = "standard_category를 포함해야 하며, 해당 카테고리의 검수 기준 내용이 출력")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<List<CsStandardBean>> getStandard(
            @RequestParam CsStandardBean.standard_category standard_category)
    {
        try {
            csStandardService.getStandard(standard_category);
            return ResponseEntity.ok(csStandardService.getStandard(standard_category));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/cs/standard/edit")
    @Operation(summary = "검수 기준 수정", description = "standard_id, " +
            "standard_category, standard_content를 받아서 수정함.")
    public ResponseEntity<String> updateStandard(@RequestBody CsStandardBean csStandardBean){
        try{
            csStandardService.updateStandard(csStandardBean);
            return ResponseEntity.ok("수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("검수 기준 수정 중 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/cs/standard/del")
    @Operation(summary = "검수 기준 삭제", description = "standard_id를 받아 해당 공지를 삭제")
    public ResponseEntity<String> deleteFaq(@RequestBody CsStandardBean csStandardBean){
        try{
            csStandardService.deleteStandard(csStandardBean);
            return new ResponseEntity<>("검수 기준이 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("검수 기준 삭제 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
