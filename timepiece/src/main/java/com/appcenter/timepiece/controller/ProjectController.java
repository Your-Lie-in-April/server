package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.config.SwaggerApiResponses;
import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import com.appcenter.timepiece.dto.project.TransferPrivilegeRequest;
import com.appcenter.timepiece.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("")
@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Just for Test!<br>
     * DB에 저장된 모든 프로젝트를 조회하여 ProjectResponse 타입의 List를 리턴합니다.
     */
    @GetMapping("/v1/projects/all")
    @Operation(summary = "프로젝트 전체 조회", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse<?>> findAllForTest() {
        return ResponseEntity.ok().body(CommonResponse.success("", projectService.findAll()));
    }

    @GetMapping("/v1/projects/members/{memberId}")
    @Operation(summary = "소속 프로젝트 전체 조회(썸네일)", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse<?>> findProjects(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(CommonResponse.success("",
                projectService.findProjects(memberId)));
    }

    // todo: Schedule 조회 로직의 작성이 선행되야 합니다.
    @GetMapping("/v1/projects/members/{memberId}/pin")
    @Operation(summary = "핀 설정된 프로젝트 조회(+시간표)", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse<?>> findPinProjects(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(CommonResponse.success("",
                projectService.findPinProjects(memberId)));
    }

    @GetMapping("/v1/projects/members/{memberId}/{keyword}")
    @Operation(summary = "유저가 가지고 있는 프로젝트 중 검색", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse<?>> searchProjects(@PathVariable Long memberId,
                                                            @PathVariable String keyword) {
        return ResponseEntity.ok().body(CommonResponse.success("",
                projectService.searchProjects(memberId, keyword)));
    }

    // todo: 해당 기능은 Project가 아닌 Member의 책임이 아닐까?
    @GetMapping("/v1/projects/{projectId}/members")
    @Operation(summary = "프로젝트에 속해있는 유저 전체 조회", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse<?>> findMembersInProject(@PathVariable Long projectId) {
        return ResponseEntity.ok().body(CommonResponse.success("",
                projectService.findMembers(projectId)));
    }

    @PostMapping("/v1/projects")
    @Operation(summary = "프로젝트 생성", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse> createProject(@RequestBody ProjectCreateUpdateRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        projectService.createProject(request, userDetails);
        return ResponseEntity.ok().body(CommonResponse.success("프로젝트 생성 성공", null));
    }

    @DeleteMapping("/v1/projects/{projectId}")
    @Operation(summary = "프로젝트 삭제", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse<?>> kickProject(@PathVariable Long projectId,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        projectService.deleteProject(projectId, userDetails);
        return ResponseEntity.ok().body(CommonResponse.success("프로젝트 삭제 성공", null));
    }

    @PutMapping("/v1/projects/{projectId}")
    @Operation(summary = "프로젝트 수정", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse<?>> updateProject(@PathVariable Long projectId,
                                                           @RequestBody ProjectCreateUpdateRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        projectService.updateProject(projectId, request, userDetails);
        return ResponseEntity.ok().body(CommonResponse.success("프로젝트 수정 성공", null));
    }

    @PostMapping("/v1/projects/{projectId}/invitation")
    @Operation(summary = "회원 초대 링크 생성", description = "")
    @SwaggerApiResponses
    public CommonResponse<?> generateInviteLink(@PathVariable Long projectId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("초대링크를 성공적으로 생성했습니다",
                projectService.generateInviteLink(projectId, userDetails));
    }

    @DeleteMapping("/v1/projects/{projectId}/members/{memberId}")
    @Operation(summary = "회원 강퇴", description = "")
    @SwaggerApiResponses
    public CommonResponse<Void> kick(@PathVariable Long projectId,
                                     @PathVariable Long memberId,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        projectService.kick(projectId, memberId, userDetails);
        return CommonResponse.success("추방되었습니다", null);
    }

    @DeleteMapping("/v1/projects/{projectId}/me")
    @Operation(summary = "프로젝트 나가기", description = "")
    @SwaggerApiResponses
    public CommonResponse<Void> goOut(@PathVariable Long projectId,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        projectService.goOut(projectId, userDetails);
        return CommonResponse.success("프로젝트에서 나갔습니다", null);
    }

    @PatchMapping("/v1/projects/{projectId}/transfer-privilege")
    @Operation(summary = "관리자 권한 양도", description = "")
    @SwaggerApiResponses
    public CommonResponse<Void> transferPrivilege(@PathVariable Long projectId,
                                                  @RequestBody TransferPrivilegeRequest request,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        projectService.transferPrivilege(projectId, request, userDetails);
        return CommonResponse.success("프로젝트 관리 권한을 양도하였습니다.", null);
    }

    @PostMapping("/v1/invitation/{url}")
    @Operation(summary = "회원 초대(추가)", description = "")
    @SwaggerApiResponses
    public CommonResponse<Void> join(@PathVariable String url,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        projectService.addUserToGroup(url, userDetails);
        return CommonResponse.success("프로젝트 멤버로 추가 되었습니다", null);
    }
}
