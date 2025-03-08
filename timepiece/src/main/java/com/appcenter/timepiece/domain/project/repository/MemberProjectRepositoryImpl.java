package com.appcenter.timepiece.domain.project.repository;

import com.appcenter.timepiece.domain.project.entity.MemberProject;
import com.appcenter.timepiece.domain.project.entity.QCover;
import com.appcenter.timepiece.domain.project.entity.QMemberProject;
import com.appcenter.timepiece.domain.project.entity.QProject;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberProjectRepositoryImpl implements MemberProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private QProject project = QProject.project;
    private QCover cover = QCover.cover;
    private QMemberProject memberProject = QMemberProject.memberProject;

    @Override
    public List<MemberProject> findByMemberIdAndIsPinnedIsTrue(Long memberId) {
        return findMemberProjects(memberId, null, true, null, false);
    }

    @Override
    public List<MemberProject> findAllByProjectId(Long projectId) {
        return findMemberProjects(null, projectId, null, null, false);
    }

    @Override
    public boolean existsByMemberIdAndProjectId(Long memberId, Long projectId) {
        return existsMemberProject(memberId, projectId, null, null, false);
    }

    @Override
    public Optional<MemberProject> findByMemberIdAndProjectId(Long memberId, Long projectId) {
        return findMemberProject(memberId, projectId, false);
    }

    private Page<MemberProject> findMemberProjects(Pageable pageable, Long memberId, Boolean isDeleted) {
        List<MemberProject> content = queryFactory.selectFrom(memberProject)
                .innerJoin(project).on(memberProject.projectId.eq(project.id))
                .leftJoin(cover).on(project.coverId.eq(cover.id))
                .where(memberProject.memberId.eq(memberId)
                        .and(memberProject.isStored.isFalse())
                        .and(project.isDeleted.eq(isDeleted)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(project.updatedAt.desc())
                .fetch();

        Long total = queryFactory.selectFrom(memberProject)
                .innerJoin(project).on(memberProject.projectId.eq(project.id))
                .where(memberProject.memberId.eq(memberId)
                        .and(memberProject.isStored.isFalse())
                        .and(project.isDeleted.eq(isDeleted)))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private List<MemberProject> findMemberProjects(Long memberId, Long projectId, Boolean isPinned, Boolean isStored,
                                                   Boolean isDeleted) {
        List<MemberProject> content = queryFactory.selectFrom(memberProject)
                .innerJoin(project).on(memberProject.projectId.eq(project.id))
                .where(memberIdEq(memberId),
                        projectIdEq(projectId),
                        isPinnedEq(isPinned),
                        isStoredEq(isStored),
                        (project.isDeleted.eq(isDeleted)))
                .fetch();

        return content;
    }

    private Optional<MemberProject> findMemberProject(Long memberId, Long projectId, Boolean isDeleted) {
        Optional<MemberProject> content = queryFactory.selectFrom(memberProject)
                .innerJoin(project).on(memberProject.projectId.eq(project.id))
                .where(memberIdEq(memberId),
                        projectIdEq(projectId),
                        (project.isDeleted.eq(isDeleted)))
                .stream().findAny();

        return content;
    }

    private boolean existsMemberProject(Long memberId, Long projectId, Boolean isPinned, Boolean isStored,
                                        Boolean isDeleted) {
        Integer content = queryFactory.selectOne().from(memberProject)
                .join(project).on(memberProject.projectId.eq(project.id))
                .where(memberProject.memberId.eq(memberId),
                        memberProject.projectId.eq(projectId),
                        (project.isDeleted.eq(isDeleted)),
                        isPinnedEq(isPinned),
                        isStoredEq(isStored)).fetchFirst();

        return content != null;
    }

    private BooleanExpression isStoredEq(Boolean isStoredCond) {
        return isStoredCond != null ? memberProject.isStored.eq(isStoredCond) : null;
    }

    private BooleanExpression isPinnedEq(Boolean isPinnedCond) {
        return isPinnedCond != null ? memberProject.isPinned.eq(isPinnedCond) : null;
    }

    private BooleanExpression memberIdEq(Long memberIdCond) {
        return memberIdCond != null ? memberProject.memberId.eq(memberIdCond) : null;
    }

    private BooleanExpression projectIdEq(Long projectIdCond) {
        return projectIdCond != null ? memberProject.projectId.eq(projectIdCond) : null;
    }
}
