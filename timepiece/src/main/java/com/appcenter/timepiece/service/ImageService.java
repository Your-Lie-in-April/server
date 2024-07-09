package com.appcenter.timepiece.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final RedisTemplate<String, byte[]> redisImageTemplate;

    public void uploadImage(String id, MultipartFile file) throws IOException {
        byte[] imageData = file.getBytes();
        redisImageTemplate.opsForValue().set("/cover-image/" + id, imageData);
    }

    public Optional<byte[]> getImage(String id) {
        return Optional.ofNullable(redisImageTemplate.opsForValue().get(id));
    }

    public MediaType getImageType(String id) {
        // 파일 확장자에 따라 이미지 타입을 설정합니다. 실제 구현 시 파일 메타데이터를 함께 저장하거나,
        // 파일명에서 확장자를 추출하는 방식으로 구현할 수 있습니다.
        if (id.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (id.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else {
            return MediaType.IMAGE_JPEG; // 기본값
        }
    }
}
