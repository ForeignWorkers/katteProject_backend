package me.soldesk.katteproject_backend.controller;

import lombok.RequiredArgsConstructor;
import me.soldesk.katteproject_backend.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadMedia(
            @RequestParam("post_id") String postId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("isStyle") Boolean isStyle
    ) {
        mediaService.saveMedia(postId, file, isStyle);
        return ResponseEntity.ok("업로드 성공");
    }
}
