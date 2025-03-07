package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.common.dto.CommonPagingResponse;
import com.appcenter.timepiece.config.TestSecurityConfig;
import com.appcenter.timepiece.dto.member.MemberResponse;
import com.appcenter.timepiece.dto.project.InvitationLinkResponse;
import com.appcenter.timepiece.dto.project.InvitationResponse;
import com.appcenter.timepiece.dto.project.PinProjectResponse;
import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import com.appcenter.timepiece.dto.project.ProjectResponse;
import com.appcenter.timepiece.dto.project.ProjectThumbnailResponse;
import com.appcenter.timepiece.dto.project.TransferPrivilegeRequest;
import com.appcenter.timepiece.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.DayOfWeek;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(TestSecurityConfig.class)
@WithMockUser
public class ProjectControllerTest {

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("프로젝트 상세 정보 조회가 성공적으로 처리되어야 한다")
    void findProjectTest() throws Exception {
        // Given
        Long projectId = 1L;
        ProjectResponse projectDetail = ProjectResponse.builder()
                .projectId(1L)
                .title("Test Project")
                .description("Project Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .daysOfWeek(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)))
                .color("#FF0000")
                .coverInfo(null)
                .build();

        when(projectService.findProject(eq(projectId), any(UserDetails.class)))
                .thenReturn(projectDetail);

        // When & Then
        mockMvc.perform(get("/v1/projects/{projectId}", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트 조회 성공"))
                .andExpect(jsonPath("$.data.projectId").value(projectId))
                .andExpect(jsonPath("$.data.description").value("Project Description"))
                .andExpect(jsonPath("$.data.startDate").exists())
                .andExpect(jsonPath("$.data.endDate").exists())
                .andExpect(jsonPath("$.data.startTime").exists())
                .andExpect(jsonPath("$.data.daysOfWeek").exists())
                .andExpect(jsonPath("$.data.color").hasJsonPath())
                .andExpect(jsonPath("$.data.coverInfo").hasJsonPath());
    }

    @Test
    @DisplayName("소속 프로젝트 전체 조회가 성공적으로 처리되어야 한다")
    void findProjectsTest() throws Exception {
        // Given
        Long memberId = 1L;
        ProjectThumbnailResponse projectThumbnail1 = ProjectThumbnailResponse.builder()
                .projectId(1L)
                .title("Test Project")
                .description("Project Description")
                .color("#FF0000")
                .build();
        ProjectThumbnailResponse projectThumbnail2 = ProjectThumbnailResponse.builder()
                .projectId(2L)
                .title("Test Project2")
                .description("Project Description2")
                .thumbnailUrl("http://example.com/thumbnail2")
                .build();
        List<ProjectThumbnailResponse> projects = Arrays.asList(projectThumbnail1, projectThumbnail2);

        given(projectService.findProjects(eq(0), eq(6), eq(memberId), any(UserDetails.class)))
                .willReturn((CommonPagingResponse) new CommonPagingResponse<>(0, 6, 2L, 1, projects));

        // When & Then
        mockMvc.perform(get("/v1/projects/members/{memberId}", memberId)
                        .param("page", "0")
                        .param("size", "6"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트 목록 조회 성공"))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(6))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.data[0].projectId").value(1))
                .andExpect(jsonPath("$.data.data[0].title").value("Test Project"))
                .andExpect(jsonPath("$.data.data[0].color").hasJsonPath())
                .andExpect(jsonPath("$.data.data[0].thumbnailUrl").hasJsonPath());
    }

    @Test
    @DisplayName("핀 설정된 프로젝트 조회가 성공적으로 처리되어야 한다")
    void findPinProjectsTest() throws Exception {
        // Given
        Long memberId = 1L;
        PinProjectResponse pinProject = PinProjectResponse.builder()
                .projectId(1L)
                .title("Pinned Project")
                .description("Pinned Project Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .daysOfWeek(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)))
                .color("#FF0000")
                .memberCount(1)
                .schedule(Collections.emptyList())
                .build();

        when(projectService.findPinProjects(eq(memberId), any(UserDetails.class)))
                .thenReturn(List.of(pinProject));

        // When & Then
        mockMvc.perform(get("/v1/projects/members/{memberId}/pin", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("핀 설정된 프로젝트 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].projectId").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Pinned Project"))
                .andExpect(jsonPath("$.data[0].schedule").exists());
    }

    @Test
    @DisplayName("프로젝트 검색이 성공적으로 처리되어야 한다")
    void searchProjectsParamTest() throws Exception {
        // Given
        Long memberId = 1L;
        String keyword = "test";
        Boolean isStored = false;
        ProjectThumbnailResponse projectThumbnail1 = ProjectThumbnailResponse.builder()
                .projectId(1L)
                .title("Test Project")
                .description("Project Description")
                .color("#FF0000")
                .build();
        ProjectThumbnailResponse projectThumbnail2 = ProjectThumbnailResponse.builder()
                .projectId(2L)
                .title("Test Project2")
                .description("Project Description2")
                .thumbnailUrl("http://example.com/thumbnail2")
                .build();
        List<ProjectThumbnailResponse> projects = Arrays.asList(projectThumbnail1, projectThumbnail2);

        when(projectService.searchProjects(eq(0), eq(6), eq(isStored), eq(memberId), eq(keyword),
                any(UserDetails.class)))
                .thenReturn((CommonPagingResponse) new CommonPagingResponse<>(0, 6, 2L, 1, projects));

        // When & Then
        mockMvc.perform(get("/v2/projects/members/{memberId}", memberId)
                        .param("page", "0")
                        .param("size", "6")
                        .param("keyword", keyword)
                        .param("isStored", isStored.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트 검색 성공"))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(6))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.data[0].projectId").value(1))
                .andExpect(jsonPath("$.data.data[0].title").value("Test Project"))
                .andExpect(jsonPath("$.data.data[0].description").value("Project Description"))
                .andExpect(jsonPath("$.data.data[0].color").hasJsonPath())
                .andExpect(jsonPath("$.data.data[0].thumbnailUrl").hasJsonPath());
    }

    @Test
    @DisplayName("프로젝트에 속한 멤버 조회가 성공적으로 처리되어야 한다")
    void findMembersInProjectTest() throws Exception {
        // Given
        Long projectId = 1L;
        MemberResponse member1 = MemberResponse.builder()
                .memberId(1L)
                .email("test01@example.com")
                .nickname("User1")
                .state("ACTIVE")
                .profileImageUrl("http://example.com/profile01")
                .isPrivileged(true)
                .build();
        MemberResponse member2 = MemberResponse.builder()
                .memberId(2L)
                .email("test02@example.com")
                .nickname("User2")
                .state("ACTIVE")
                .profileImageUrl("http://example.com/profile02")
                .isPrivileged(false)
                .build();
        List<MemberResponse> members = List.of(member1, member2);

        when(projectService.findMembers(eq(projectId), any(UserDetails.class)))
                .thenReturn(members);

        // When & Then
        mockMvc.perform(get("/v1/projects/{projectId}/members", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트 내 사용자 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].memberId").value(1))
                .andExpect(jsonPath("$.data[0].nickname").value("User1"))
                .andExpect(jsonPath("$.data[0].state").value("ACTIVE"))
                .andExpect(jsonPath("$.data[0].profileImageUrl").value("http://example.com/profile01"))
                .andExpect(jsonPath("$.data[0].isPrivileged").value(true));
    }

    @Test
    @DisplayName("보관 프로젝트 목록 조회가 성공적으로 처리되어야 한다")
    void findStoredProjectsTest() throws Exception {
        // Given
        ProjectThumbnailResponse projectThumbnail1 = ProjectThumbnailResponse.builder()
                .projectId(1L)
                .title("Test Project")
                .description("Project Description")
                .color("#FF0000")
                .build();
        ProjectThumbnailResponse projectThumbnail2 = ProjectThumbnailResponse.builder()
                .projectId(2L)
                .title("Test Project2")
                .description("Project Description2")
                .thumbnailUrl("http://example.com/thumbnail2")
                .build();
        List<ProjectThumbnailResponse> projects = Arrays.asList(projectThumbnail1, projectThumbnail2);

        // Use raw type with unchecked cast to bypass generic type checking
        when(projectService.findStoredProjects(eq(0), eq(6), any(UserDetails.class)))
                .thenReturn((CommonPagingResponse) new CommonPagingResponse<>(0, 6, 2L, 1, projects));

        // When & Then
        mockMvc.perform(get("/v1/projects/stored")
                        .param("page", "0")
                        .param("size", "6"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("보관 프로젝트 목록 조회 성공"))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(6))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.data[0].projectId").value(1))
                .andExpect(jsonPath("$.data.data[0].title").value("Test Project"))
                .andExpect(jsonPath("$.data.data[0].description").value("Project Description"))
                .andExpect(jsonPath("$.data.data[0].color").hasJsonPath())
                .andExpect(jsonPath("$.data.data[0].thumbnailUrl").hasJsonPath());
    }

    @Test
    @DisplayName("프로젝트 생성이 성공적으로 처리되어야 한다")
    void createProjectTest() throws Exception {
        // Given
        ProjectCreateUpdateRequest request = ProjectCreateUpdateRequest.builder()
                .title("New Project")
                .description("New Project Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .daysOfWeek(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)))
                .color("#FF0000")
                .coverImageId("")
                .build();

        doNothing().when(projectService).createProject(any(ProjectCreateUpdateRequest.class), any(UserDetails.class));

        // When & Then
        mockMvc.perform(post("/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트 생성 성공"));
    }

    @Test
    @DisplayName("프로젝트 삭제가 성공적으로 처리되어야 한다")
    void kickProjectTest() throws Exception {
        // Given
        Long projectId = 1L;

        doNothing().when(projectService).deleteProject(eq(projectId), any(UserDetails.class));

        // When & Then
        mockMvc.perform(delete("/v1/projects/{projectId}", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트 삭제 성공"));
    }

    @Test
    @DisplayName("회원 초대 링크 생성이 성공적으로 처리되어야 한다")
    void generateInviteLinkTest() throws Exception {
        // Given
        Long projectId = 1L;
        String title = "Test Project";
        String url = "http://example.com/invite/abc123";
        InvitationLinkResponse response = InvitationLinkResponse.builder()
                .projectId(projectId)
                .title(title)
                .link(url)
                .build();

        when(projectService.generateInviteLink(eq(projectId), any(UserDetails.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/v1/projects/{projectId}/invitation", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("초대링크를 성공적으로 생성했습니다"))
                .andExpect(jsonPath("$.data.link").value(url));
    }

    @Test
    @DisplayName("초대 링크 메타데이터 조회가 성공적으로 처리되어야 한다")
    void decodeInviteLinkTest() throws Exception {
        // Given
        String url = "abc123";
        String title = "Test Project";
        String invitator = "User1";
        InvitationResponse response = InvitationResponse.builder()
                .title(title)
                .invitator(invitator)
                .isExpired(false)
                .build();

        when(projectService.decodeInviteLink(eq(url)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/v1/projects/invitations")
                        .param("url", url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("초대링크 정보를 성공적으로 조회했습니다"))
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.invitator").value(invitator));
    }

    @Test
    @DisplayName("회원 강퇴가 성공적으로 처리되어야 한다")
    void kickTest() throws Exception {
        // Given
        Long projectId = 1L;
        Long memberId = 2L;

        doNothing().when(projectService).kick(eq(projectId), eq(memberId), any(UserDetails.class));

        // When & Then
        mockMvc.perform(delete("/v1/projects/{projectId}/members/{memberId}", projectId, memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("추방되었습니다"));
    }

    @Test
    @DisplayName("프로젝트 나가기가 성공적으로 처리되어야 한다")
    void goOutTest() throws Exception {
        // Given
        Long projectId = 1L;

        doNothing().when(projectService).goOut(eq(projectId), any(UserDetails.class));

        // When & Then
        mockMvc.perform(delete("/v1/projects/{projectId}/me", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트에서 나갔습니다"));
    }

    @Test
    @DisplayName("관리자 권한 양도가 성공적으로 처리되어야 한다")
    void transferPrivilegeTest() throws Exception {
        // Given
        Long projectId = 1L;
        Long toMemberId = 2L;
        TransferPrivilegeRequest request = new TransferPrivilegeRequest(toMemberId);

        doNothing().when(projectService)
                .transferPrivilege(eq(projectId), any(TransferPrivilegeRequest.class), any(UserDetails.class));

        // When & Then
        mockMvc.perform(patch("/v1/projects/{projectId}/transfer-privilege", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("프로젝트 관리 권한을 양도하였습니다."));
    }
}