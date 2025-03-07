package com.appcenter.timepiece.repository;

import static com.appcenter.timepiece.domain.QCover.cover;

import com.appcenter.timepiece.domain.Project;
import com.appcenter.timepiece.domain.QMember;
import com.appcenter.timepiece.domain.QMemberProject;
import com.appcenter.timepiece.domain.QProject;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private QProject project = QProject.project;
    private QMember member = QMember.member;
    private QMemberProject memberProject = QMemberProject.memberProject;

    @Override
    public Page<Project> searchProjects(Long memberId, String keyword, Boolean isStored, Pageable pageable) {
        return findProjects(memberId, keyword, isStored, false, pageable);
    }

    @Override
    public Page<Project> findProjectIsStored(Long memberId, Pageable pageable) {
        return findProjects(memberId, null, true, false, pageable);
    }

    public Page<Project> findProjects(Long memberId, String keyword, Boolean isStored, Boolean isDeleted,
                                      Pageable pageable) {
        // ID 참조 방식으로 조인하는 컨텐츠 쿼리
        List<Project> content = queryFactory
                .selectDistinct(project)
                .from(project)
                .join(memberProject).on(project.id.eq(memberProject.projectId))
                .where(memberProject.memberId.eq(memberId),
                        isStoredEq(isStored),
                        isDeletedEq(isDeleted),
                        keywordLike(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(project.updatedAt.desc()) // 기본 정렬 추가
                .fetch();

        // 카운트 쿼리
        Long total = queryFactory
                .select(project.countDistinct())
                .from(project)
                .join(memberProject).on(project.id.eq(memberProject.projectId))
                .where(memberProject.memberId.eq(memberId),
                        isStoredEq(isStored),
                        isDeletedEq(isDeleted),
                        keywordLike(keyword))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    public Page<ProjectWithCoverDTO> searchProjectsWithCover(Long memberId, String keyword, Boolean isStored,
                                                             Pageable pageable) {
        // 쿼리 1: 프로젝트 목록과 커버 ID 조회
        List<Tuple> tuples = queryFactory
                .select(project, cover.thumbnailUrl)
                .from(project)
                .join(memberProject).on(project.id.eq(memberProject.projectId))
                .leftJoin(cover).on(project.coverId.eq(cover.id))
                .where(memberProject.memberId.eq(memberId),
                        isStoredEq(isStored),
                        project.isDeleted.isFalse(),
                        keywordLike(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(project.updatedAt.desc())
                .fetch();

        // 결과를 DTO로 변환
        List<ProjectWithCoverDTO> content = tuples.stream()
                .map(tuple -> new ProjectWithCoverDTO(
                        tuple.get(project),
                        tuple.get(cover.thumbnailUrl)))
                .collect(Collectors.toList());

        // 카운트 쿼리
        Long total = queryFactory
                .select(project.countDistinct())
                .from(project)
                .join(memberProject).on(project.id.eq(memberProject.projectId))
                .where(memberProject.memberId.eq(memberId),
                        isStoredEq(isStored),
                        project.isDeleted.isFalse(),
                        keywordLike(keyword))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression isDeletedEq(Boolean isDeletedCond) {
        return isDeletedCond != null ? project.isDeleted.eq(isDeletedCond) : null;
    }

    private BooleanExpression isStoredEq(Boolean isStoredCond) {
        return isStoredCond != null ? memberProject.isStored.eq(isStoredCond) : null;
    }

    private BooleanExpression keywordLike(String keywordCond) {
        return keywordCond != null ? project.title.containsIgnoreCase(keywordCond) : null;
    }
}
