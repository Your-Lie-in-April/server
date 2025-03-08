package com.appcenter.timepiece.domain.project.controller;

import com.appcenter.timepiece.domain.project.service.ImageService;
import com.appcenter.timepiece.global.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "이미지 등록(redis)")
    @PostMapping(value = "/v1/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<?> uploadImage(@RequestPart("thumbnail") MultipartFile thumbnail,
                                         @RequestPart("coverImage") MultipartFile coverImage) {
        try {
            imageService.uploadImage(thumbnail, coverImage);
            return CommonResponse.success("이미지를 성공적으로 업로드하였습니다.", null);
        } catch (IOException e) {
            return CommonResponse.error("이미지 업로드에 실패했습니다.", null);
        }
    }

    @Operation(summary = "이미지 조회(Get)", hidden = true)
    @GetMapping("/cover-image/{id}")
    public ResponseEntity<byte[]> getCoverImage(@PathVariable String id) {
        Optional<byte[]> imageData = imageService.getImage(id);
        MediaType imageType = imageService.getImageType(id);
        if (imageData.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;")
                    .contentType(imageType) // 이미지 타입에 맞춰 변경 가능
                    .body(imageData.get());
        }
        return ResponseEntity.notFound().build();
    }
}
