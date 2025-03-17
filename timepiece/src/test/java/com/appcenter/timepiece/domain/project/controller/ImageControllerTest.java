package com.appcenter.timepiece.domain.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appcenter.timepiece.domain.project.service.ImageService;
import com.appcenter.timepiece.global.config.TestSecurityConfig;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ImageController.class)
@Import(TestSecurityConfig.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    @DisplayName("이미지 업로드가 성공적으로 처리되어야 한다")
    void uploadImageSuccessTest() throws Exception {
        // Given
        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnail",
                "thumbnail.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "thumbnail content".getBytes()
        );

        MockMultipartFile coverImage = new MockMultipartFile(
                "coverImage",
                "cover.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "cover content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/v1/image")
                        .file(thumbnail)
                        .file(coverImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("이미지를 성공적으로 업로드하였습니다."));
    }

    @Test
    @DisplayName("이미지 업로드 실패 시 에러 응답을 반환해야 한다")
    void uploadImageFailureTest() throws Exception {
        // Given
        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnail",
                "thumbnail.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "thumbnail content".getBytes()
        );

        MockMultipartFile coverImage = new MockMultipartFile(
                "coverImage",
                "cover.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "cover content".getBytes()
        );

        willThrow(new java.io.IOException("Upload failed")).given(imageService).uploadImage(any(), any());

        // When & Then
        mockMvc.perform(multipart("/v1/image")
                        .file(thumbnail)
                        .file(coverImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("이미지 업로드에 실패했습니다."));
    }

    @Test
    @DisplayName("커버 이미지 조회가 성공적으로 처리되어야 한다")
    void getCoverImageSuccessTest() throws Exception {
        // Given
        String imageId = "123";
        byte[] imageData = "image content".getBytes();

        given(imageService.getImage(eq(imageId))).willReturn(Optional.of(imageData));
        given(imageService.getImageType(eq(imageId))).willReturn(MediaType.IMAGE_JPEG);

        // When & Then
        mockMvc.perform(get("/cover-image/{id}", imageId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline;"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageData));
    }

    @Test
    @DisplayName("존재하지 않는 이미지 조회 시 404 응답을 반환해야 한다")
    void getCoverImageNotFoundTest() throws Exception {
        // Given
        String imageId = "nonexistent";

        given(imageService.getImage(eq(imageId))).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/cover-image/{id}", imageId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}