package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.dto.project.TransferPrivilegeRequest;
import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import com.appcenter.timepiece.service.ProjectService;
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
    public ResponseEntity<CommonResponse<?>> findAllForTest() {
        return ResponseEntity.ok().body(CommonResponse.success("", projectService.findAll()));
    }

    @GetMapping("/v1/projects/members/{memberId}")
    public ResponseEntity<CommonResponse<?>> findProjects(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(CommonResponse.success("",
                projectService.findProjects(memberId)));
    }

    // todo: Schedule 조회 로직의 작성이 선행되야 합니다.
    @GetMapping("/v1/projects/members/{memberId}/pin")
    public ResponseEntity<CommonResponse<?>> findPinProjects(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(CommonResponse.success("",
                projectService.findPinProjects(memberId)));
    }

    @GetMapping("/v1/projects/members/{memberId}/{keyword}")
    public ResponseEntity<CommonResponse<?>> searchProjects(@PathVariable Long memberId,
                                                            @PathVariable String keyword) {
        return ResponseEntity.ok().body(CommonResponse.success("",
                projectService.searchProjects(memberId, keyword)));
    }

    // todo: 해당 기능은 Project가 아닌 Member의 책임이 아닐까?
    @GetMapping("/v1/projects/{projectId}/members")
    public ResponseEntity<CommonResponse<?>> findMembersInProject(@PathVariable Long projectId) {
        return ResponseEntity.ok().body(CommonResponse.success("",
                projectService.findMembers(projectId)));
    }

    @PostMapping("/v1/projects")
    public ResponseEntity<CommonResponse> createProject(@RequestBody ProjectCreateUpdateRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        projectService.createProject(request, userDetails);
        return ResponseEntity.ok().body(CommonResponse.success("프로젝트 생성 성공", null));
    }

    @DeleteMapping("/v1/projects/{projectId}")
    public ResponseEntity<CommonResponse<?>> kickProject(@PathVariable Long projectId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        projectService.deleteProject(projectId, userDetails);
        return ResponseEntity.ok().body(CommonResponse.success("프로젝트 삭제 성공", null));
    }

    @PutMapping("/v1/projects/{projectId}")
    public ResponseEntity<CommonResponse<?>> updateProject(@PathVariable Long projectId,
                                              @RequestBody ProjectCreateUpdateRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        projectService.updateProject(projectId, request, userDetails);
        return ResponseEntity.ok().body(CommonResponse.success("프로젝트 수정 성공", null));
    }

    @PostMapping("/v1/projects/{projectId}/invitation")
    public CommonResponse<?> generateInviteLink(@PathVariable Long projectId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("초대링크를 성공적으로 생성했습니다",
                projectService.generateInviteLink(projectId, userDetails));
    }

    @DeleteMapping("/v1/projects/{projectId}/members/{memberId}")
    public CommonResponse<Void> kick(@PathVariable Long projectId,
                                     @PathVariable Long memberId,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        projectService.kick(projectId, memberId, userDetails);
        return CommonResponse.success("추방되었습니다", null);
    }

    @DeleteMapping("/v1/projects/{projectId}/me")
    public CommonResponse<Void> goOut(@PathVariable Long projectId,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        projectService.goOut(projectId, userDetails);
        return CommonResponse.success("프로젝트에서 나갔습니다", null);
    }

    @PatchMapping("/v1/projects/{projectId}/transfer-privilege")
    public CommonResponse<Void> transferPrivilege(@PathVariable Long projectId,
                                                  @RequestBody TransferPrivilegeRequest request,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        projectService.transferPrivilege(projectId, request, userDetails);
        return CommonResponse.success("프로젝트 관리 권한을 양도하였습니다.", null);
    }

    @PostMapping("/v1/invitation/{url}")
    public CommonResponse<Void> join(@PathVariable String url,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        projectService.addUserToGroup(url, userDetails);
        return CommonResponse.success("프로젝트 멤버로 추가 되었습니다", null);
    }
}
