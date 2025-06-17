package me.soldesk.katteproject_backend.controller;

import common.bean.content.ContentShortformBean;
import common.bean.content.ContentStyleBean;
import common.bean.content.ContentStyleComment;
import common.bean.content.ContentStyleProductJoinBean;
import common.bean.product.ProductPerSaleBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.soldesk.katteproject_backend.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping("/content/short")
    //API Docs
    @Operation(summary = "숏폼 등록", description = "숏폼을 새롭게 등록합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> createContent(@RequestBody ContentShortformBean content) {
        contentService.addShortform(content);
        return ResponseEntity.ok("숏폼 등록이 되었습니다.");
    }

    @GetMapping("/content/short/id")
    //API Docs
    @Operation(summary = "숏폼 id 조회", description = "숏폼을 id를 이용해서 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<ContentShortformBean> createContent(@RequestParam String id) {
        ContentShortformBean result = contentService.getShortformById(Integer.parseInt(id));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/content/short/products")
    //API Docs
    @Operation(summary = "숏폼 product 조회", description = "숏폼을 product_id 를 이용해서 조회합니다. (List로 반환)")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<ContentShortformBean>> getContent(@RequestParam String product_id,
                                                                 @RequestParam(defaultValue = "10") int count,
                                                                 @RequestParam(defaultValue = "0") int offset) {
        List<ContentShortformBean> result = contentService.getShortformsByProjectId(Integer.parseInt(product_id), count, offset);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/content/short/latest")
    @Operation(summary = "최근 숏폼 ID 조회")
    public ResponseEntity<Integer> getLatestShortformId() {
        int latestId = contentService.getLatestShortformId();
        return ResponseEntity.ok(latestId); // ← 바로 latestId 리턴
    }

    @PostMapping("/content/style")
    //API Docs
    @Operation(summary = "스타일 등록", description = "스타일 게시물을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Integer> addContentStyle(@RequestBody ContentStyleBean styleBean) {
        // 1) 스타일 저장 후 생성된 ID를 반환받아 변수에 담고
        int contentId = contentService.addStyleAndReturnId(styleBean);
        // 2) 해시태그 리스트와 함께 join 테이블에 연결
        contentService.linkStyleWithHashtag(contentId, styleBean.getHashtags());

        return ResponseEntity.ok(contentId);
    }

    @GetMapping("/content/style/hashtag")
    //API Docs
    @Operation(summary = "스타일을 해쉬태그로 조회.", description = "특정 해쉬태그가 이어진 스타일 리스트로 반환")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<ContentStyleBean>> getStyleByHashtag(@RequestParam String hashtag,
                                                                    @RequestParam(defaultValue = "10") int count,
                                                                    @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(contentService.getStyleByHashtag(hashtag, count, offset));
    }

    @GetMapping("/content/style")
    //API Docs
    @Operation(summary = "스타일을 id로 조회", description = "스타일 id 값 입력")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<ContentStyleBean> getStyleById(@RequestParam int style_id) {
        return ResponseEntity.ok(contentService.getStyleById(style_id));
    }

    @PostMapping("/content/style/comment")
    //API Docs
    @Operation(summary = "스타일의 댓글을 작성", description = "comment bean 형태를 바디로")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<String> addContentStyleComment(@RequestBody ContentStyleComment comment) {
        contentService.addStyleComment(comment);
        return ResponseEntity.ok("댓글 등록이 완료되었습니다.");
    }

    @GetMapping("/content/style/comment")
    //API Docs
    @Operation(summary = "스타일 댓글을 스타일 id로 조회", description = "style 에 달린 댓글을 스타일 id로 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<ContentStyleComment>> getStyleCommentByComment(@RequestParam String style_id){
        return ResponseEntity.ok(contentService.getStyleCommentById(Integer.parseInt(style_id)));
    }

    @GetMapping("/content/style/user")
    //API Docs
    @Operation(summary = "특정 유저가 올린 스타일 조회", description = "유저 id로 스타일 조회 (리스트 반환)")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<ContentStyleBean>> getStyleByUserId(@RequestParam int user_id,
                                                                   @RequestParam(defaultValue = "10") int count,
                                                                   @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(contentService.getStyleByUserId(user_id, count, offset));
    }

    @GetMapping("/content/style/user/count")
    @Operation(summary = "유저가 작성한 스타일 총 개수", description = "유저 ID로 등록된 스타일의 총 개수를 반환")
    public ResponseEntity<Integer> getStyleCount(@RequestParam int user_id) {
        return ResponseEntity.ok(contentService.getStyleCountByUserId(user_id));
    }

    @GetMapping("/content/style/comment/user")
    //API Docs
    @Operation(summary = "스타일 댓글을 유저 id로 조회", description = "유저 id로 댓글 조회 (리스트 조회)")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<ContentStyleComment>> getStyleCommentByUserId(@RequestParam int user_id,
                                                                             @RequestParam(defaultValue = "10") int count,
                                                                             @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(contentService.getStyleCommentByUserId(user_id, count, offset));
    }

    @GetMapping("/content/style/comment/user/count")
    @Operation(summary = "유저가 작성한 스타일 댓글 총 개수", description = "유저 ID로 등록된 스타일 댓글의 총 개수를 반환")
    public ResponseEntity<Integer> getStyleCommentCount(@RequestParam int user_id) {
        return ResponseEntity.ok(contentService.getStyleCommentCountByUserId(user_id));
    }

    @PostMapping("/content/style/like")
    //API Docs
    @Operation(summary = "스타일 좋아요 토글", description = "특정 스타일에 대한 좋아요 토글")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Boolean> toggleStyleLike(@RequestParam int style_id, @RequestParam int user_id) {
        boolean liked = contentService.toggleStyleLike(style_id, user_id);
        return ResponseEntity.ok(liked);
    }

    @GetMapping("/content/style/like/user")
    //API Docs
    @Operation(summary = "특정 유저가 좋아요 누른 스타일 조회", description = "특정 유저가 좋아요 누른 스타일을 조회 (리스트)")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<ContentStyleBean>> getLikedStyles(@RequestParam int user_id,
                                                                 @RequestParam(defaultValue = "10") int count,
                                                                 @RequestParam(defaultValue = "0") int offset) {

        List<ContentStyleBean> likedStyles = contentService.getLikedStyles(user_id, count, offset);
        return ResponseEntity.ok(likedStyles);
    }

    @GetMapping("/content/style/like/userAll")
    //API Docs
    @Operation(summary = "특정 유저가 좋아요 누른 스타일 조회", description = "특정 유저가 좋아요 누른 스타일을 조회 (리스트)")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<List<ContentStyleBean>> getLikedStylesAll(@RequestParam int user_id) {

        List<ContentStyleBean> likedStyles = contentService.getStyleByUserAll(user_id);
        return ResponseEntity.ok(likedStyles);
    }

    @PatchMapping("/content/short/like")
    //API Docs
    @Operation(summary = "숏폼 좋아요 토글", description = "특정 숏폼에 대한 좋아요 토글")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<Boolean> toggleShortLike(@RequestParam int short_id, @RequestParam int user_id) {
        boolean liked = contentService.toggleShortLike(short_id, user_id);
        return ResponseEntity.ok(liked);
    }

    @GetMapping("/content/short/one_random")
    @Operation(summary = "숏폼 좋아요 토글", description = "특정 숏폼에 대한 좋아요 토글")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    public ResponseEntity<ContentShortformBean> getOneRandom() {
        return ResponseEntity.ok(contentService.getShortOneRandom());
    }

    @GetMapping("/content/style/recent")
    public ResponseEntity<List<ContentStyleBean>> recentStyles(
            @RequestParam(name = "size",   defaultValue = "10")  int size,
            @RequestParam(name = "offset", defaultValue = "0")   int offset
    ) {
        List<ContentStyleBean> list = contentService.getRecentStylesByOffset(size, offset);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/content/style/add_product_id")
    public ResponseEntity<Boolean> addProductId(@RequestParam int style_id, @RequestParam int product_id) {
        try {
            contentService.insertStyleProductTag(style_id, product_id);
            return ResponseEntity.ok(true);
        }catch (Exception e) {
            System.out.println("스타일에 프로덕션 아이디 목록을 생성하는데 실패하였습니다." + e.getMessage());
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/content/perSaleShortId")
    public ResponseEntity<ProductPerSaleBean> getPerSaleShortId(@RequestParam int short_id) {
        return ResponseEntity.ok(contentService.getProductPerSaleByShortId(short_id));
    }

    @GetMapping("/content/styleProductJoin")
    public ResponseEntity<List<ContentStyleProductJoinBean>> getStyleProductJoin(@RequestParam int product_id) {
        return ResponseEntity.ok(contentService.getProductPerSaleByProductId(product_id));
    }
}
