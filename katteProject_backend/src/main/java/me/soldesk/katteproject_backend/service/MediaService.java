package me.soldesk.katteproject_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class MediaService {

    @Value("${media.base-path}")
    private String basePath;

    public void saveMedia(String postId, MultipartFile file, Boolean isStyle) {
        try {
            String contentType = file.getContentType();
            String extension = getExtensionFromContentType(contentType);
            boolean isVideo = contentType != null && contentType.startsWith("video");

            // postId가 "182_1"이면 폴더명은 "182"
            String[] parts = postId.split("_");
            String folderName = parts[0]; // "182"

            String folder = isVideo
                    ? "mp4" : isStyle ? "style" : "images";

            // 저장 경로 설정: media/images/182/
            Path saveDir = Paths.get(basePath, folder, folderName);
            Files.createDirectories(saveDir);

            // 파일 이름은 그대로 postId 기반
            String filename = (isVideo ? "short_" : "") + postId + extension;
            Path filePath = saveDir.resolve(filename);

            file.transferTo(filePath.toFile());

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) return "";
        switch (contentType) {
            case "image/jpeg": return ".jpg";
            case "image/png": return ".png";
            case "image/webp": return ".webp";
            case "video/mp4": return ".mp4";
            case "video/quicktime": return ".mov";
            default: return "";
        }
    }
}
