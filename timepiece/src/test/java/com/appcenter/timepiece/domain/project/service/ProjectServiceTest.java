package com.appcenter.timepiece.domain.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;

import com.appcenter.timepiece.domain.project.dto.ProjectThumbnailResponse;
import com.appcenter.timepiece.domain.project.repository.MemberProjectRepository;
import com.appcenter.timepiece.global.common.dto.CommonPagingResponse;
import com.appcenter.timepiece.global.exception.ExceptionMessage;
import com.appcenter.timepiece.global.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.global.security.CustomUserDetails;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private MemberProjectRepository memberProjectRepository;

    @Spy
    @InjectMocks
    private ProjectService projectService;  // 실제 서비스 클래스 이름에 맞게 수정 필요

    @Nested
    @DisplayName("findProjects 메서드는")
    class FindProjectsTest {

        private Long memberId;
        private Integer page;
        private Integer size;
        private List<ProjectThumbnailResponse> projectList;
        private PageRequest pageable;
        private CustomUserDetails userDetails;

        @BeforeEach
        void setUp() {
            // given
            memberId = 1L;
            page = 0;
            size = 10;
            pageable = PageRequest.of(page, size);
            userDetails = mock(CustomUserDetails.class);

            // 테스트용 ProjectThumbnailResponse 객체 생성
            projectList = List.of(
                    mock(ProjectThumbnailResponse.class),
                    mock(ProjectThumbnailResponse.class)
            );
        }

        @Test
        @DisplayName("유효한 사용자 요청시 페이징된 프로젝트 목록을 반환한다")
        void returnsPagedProjectsForValidUser() {
            // given
            // userDetails가 memberId의 소유자임을 설정
            given(userDetails.getId()).willReturn(memberId);

            Page<ProjectThumbnailResponse> projectPage = new PageImpl<>(
                    projectList, pageable, projectList.size()
            );

            given(memberProjectRepository.fetchProjectThumbnailResponse(pageable, memberId))
                    .willReturn(projectPage);

            // when
            CommonPagingResponse<?> result = projectService.findProjects(page, size, memberId, userDetails);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCurrentPage()).isEqualTo(page);
            assertThat(result.getPageSize()).isEqualTo(size);
            assertThat(result.getTotalCount()).isEqualTo(projectList.size());
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getData()).isEqualTo(projectList);

            // 호출 검증
            then(memberProjectRepository).should().fetchProjectThumbnailResponse(pageable, memberId);
        }

        @Test
        @DisplayName("권한이 없는 사용자 요청시 예외를 발생시킨다")
        void throwsExceptionWhenUserHasNoPermission() {
            // given
            // userDetails가 memberId의 소유자가 아님을 설정
            Long differentUserId = 999L;
            given(userDetails.getId()).willReturn(differentUserId);

            // when & then
            assertThatThrownBy(() -> projectService.findProjects(page, size, memberId, userDetails))
                    .isInstanceOf(NotEnoughPrivilegeException.class)
                    .hasMessageContaining(ExceptionMessage.MEMBER_UNAUTHENTICATED.getMessage());

            // 호출되지 않아야 함을 검증
            then(memberProjectRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("프로젝트가 없을 경우 빈 페이징 응답을 반환한다")
        void returnsEmptyResponseWhenNoProjects() {
            // given
            // userDetails가 memberId의 소유자임을 설정
            given(userDetails.getId()).willReturn(memberId);

            Page<ProjectThumbnailResponse> emptyPage = new PageImpl<>(
                    List.of(), pageable, 0
            );

            given(memberProjectRepository.fetchProjectThumbnailResponse(pageable, memberId))
                    .willReturn(emptyPage);

            // when
            CommonPagingResponse<?> result = projectService.findProjects(page, size, memberId, userDetails);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCurrentPage()).isEqualTo(page);
            assertThat(result.getPageSize()).isEqualTo(size);
            assertThat(result.getTotalCount()).isZero();
            assertThat(result.getTotalPages()).isZero();
        }
    }
}