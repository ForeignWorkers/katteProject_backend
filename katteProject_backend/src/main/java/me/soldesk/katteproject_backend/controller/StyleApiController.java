package me.soldesk.katteproject_backend.controller;

import common.bean.content.ContentStyleBean;
import me.soldesk.katteproject_backend.service.StyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/styles")
@CrossOrigin(origins = "http://localhost:8080")  // 클라이언트 도메인 허용
public class StyleApiController {

    @Autowired
    private final StyleService styleService;

    public StyleApiController(StyleService styleService) {
        this.styleService = styleService;
    }

    /**
     * 페이징된 스타일 목록 + imageUrls 함께 반환
     * GET /api/styles?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<List<ContentStyleBean>> listStyles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<ContentStyleBean> styles = styleService.getPage(page, size);
        return ResponseEntity.ok(styles);
    }

    @GetMapping("/count")
    public int countStylesByUserId(@RequestParam("user_id") int user_id) {
        int count = styleService.countStyleByUserId(user_id);
        return count;
    }

    @GetMapping("/get")
    public ResponseEntity<List<ContentStyleBean>> listStylesByUserId(
            @RequestParam("user_id") int user_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<ContentStyleBean> styles = styleService.getStyleByUserId(user_id, page, size);
        return ResponseEntity.ok(styles);
    }


}
