package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "이미지 등록(redis)")
    @PostMapping(value="/v1/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestPart("thumbnail") MultipartFile thumbnail,
                                         @RequestPart("coverImage") MultipartFile coverImage) {
        try {
            imageService.uploadImage(thumbnail, coverImage);
            return ResponseEntity.ok(CommonResponse.success("Image uploaded successfully", null));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(CommonResponse.error("Failed to upload image", null));
        }
    }

    @Operation(summary = "이미지 조회(Get)", hidden = true)
    @GetMapping("/cover-image/{id}")
    public ResponseEntity<byte[]> getCoverImage(@PathVariable String id) {
        Optional<byte[]> imageData = imageService.getImage(id);
        MediaType imageType = imageService.getImageType(id);
        if (imageData.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + id + "\"")
                    .contentType(imageType) // 이미지 타입에 맞춰 변경 가능
                    .body(imageData.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
