package com.appcenter.timepiece.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appcenter.timepiece.config.TestSecurityConfig;
import com.appcenter.timepiece.domain.auth.controller.AuthController;
import com.appcenter.timepiece.domain.auth.dto.TokenResponse;
import com.appcenter.timepiece.domain.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("토큰 재발급 요청이 성공적으로 처리되어야 한다")
    void reissueAccessTokenTest() throws Exception {
        // Given
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";
        TokenResponse response = TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        given(authService.reissueAccessToken(any())).willReturn(response);

        // When & Then
        mockMvc.perform(post("/v1/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("토큰 재발급 성공"))
                .andExpect(jsonPath("$.data.accessToken").value(newAccessToken))
                .andExpect(jsonPath("$.data.refreshToken").value(newRefreshToken));
    }

}