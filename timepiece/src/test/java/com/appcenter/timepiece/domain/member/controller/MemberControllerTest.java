package com.appcenter.timepiece.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appcenter.timepiece.domain.member.dto.MemberResponse;
import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.domain.member.service.MemberService;
import com.appcenter.timepiece.domain.project.service.ProjectService;
import com.appcenter.timepiece.global.config.TestSecurityConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@Import(TestSecurityConfig.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private ProjectService projectService;

    @Test
    @DisplayName("모든 멤버 조회가 성공적으로 처리되어야 한다")
    void allUsersTest() throws Exception {
        // Given
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        List<MemberResponse> members = List.of(MemberResponse.from(member));
        given(memberService.getAllMember()).willReturn(members);

        // When & Then
        mockMvc.perform(get("/v1/members/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("(테스트)멤버 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].memberId").hasJsonPath())
                .andExpect(jsonPath("$.data[0].nickname").value("namu"));
    }

    @Test
    @DisplayName("특정 멤버 정보 조회가 성공적으로 처리되어야 한다")
    void memberInfoTest() throws Exception {
        // Given
        Long memberId = 1L;
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));

        MemberResponse memberInfo = MemberResponse.from(member);

        given(memberService.getMemberInfo(eq(memberId))).willReturn(memberInfo);

        // When & Then
        mockMvc.perform(get("/v1/members/{memberId}", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("멤버 정보 조회 성공"))
                .andExpect(jsonPath("$.data.memberId").hasJsonPath())
                .andExpect(jsonPath("$.data.nickname").value("namu"));
    }

    @Test
    @DisplayName("닉네임 재설정 V2 요청이 성공적으로 처리되어야 한다")
    void editUserNickname2Test() throws Exception {
        // Given
        Long projectId = 1L;
        String nickname = "NewNickname";
        Member member = new Member(null, nickname, "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        MemberResponse response = MemberResponse.from(member);

        given(memberService.editMemberNickname(eq(projectId), eq(nickname), any()))
                .willReturn(response);

        // When & Then
        mockMvc.perform(put("/v2/projects/members/nickname")
                        .param("projectId", projectId.toString())
                        .param("nickname", nickname))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("사용자 닉네임 수정 성공했습니다."))
                .andExpect(jsonPath("$.data.nickname").value(nickname));
    }

    @Test
    @DisplayName("상태메시지 설정 요청이 성공적으로 처리되어야 한다")
    void editUserStateTest() throws Exception {
        // Given
        String state = "Working hard";

        willDoNothing().given(memberService).editMemberState(eq(state), any(UserDetails.class));

        // When & Then
        mockMvc.perform(put("/v1/members/{state}", state))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("상태메시지 설정 성공"));
    }

    @Test
    @DisplayName("프로젝트 보관설정/해제 V2 요청이 성공적으로 처리되어야 한다")
    void storeProject2Test() throws Exception {
        // Given
        Long projectId = 1L;

        given(memberService.storeProject2(eq(projectId), any()))
                .willReturn(true);

        // When & Then
        mockMvc.perform(patch("/v2/members/storage/{projectId}", projectId)
                        .with(oauth2Login()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트 보관상태 변경"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("프로젝트 핀 설정/해제 요청이 성공적으로 처리되어야 한다")
    void pinProjectTest() throws Exception {
        // Given
        Long projectId = 1L;

        willDoNothing().given(projectService).pinProject(eq(projectId), any(UserDetails.class));

        // When & Then
        mockMvc.perform(patch("/v1/members/pin/{projectId}", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트 핀 상태 변경"));
    }
}