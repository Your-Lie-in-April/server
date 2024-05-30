package com.appcenter.timepiece.repository.impl;

import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.generated.com.appcenter.timepiece.domain.QCover;
import com.appcenter.timepiece.generated.com.appcenter.timepiece.domain.QMemberProject;
import com.appcenter.timepiece.generated.com.appcenter.timepiece.domain.QProject;
import com.appcenter.timepiece.repository.customRepository.CustomMemberProjectRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberProjectRepositoryImpl implements CustomMemberProjectRepository {

    private final JPAQueryFactory queryFactory;

    private QProject project = QProject.project;
    private QCover cover = QCover.cover;
    private QMemberProject memberProject = QMemberProject.memberProject;

    @Override
    public Page<MemberProject> findMemberProjectsWithProjectAndCover(Pageable pageable, Long memberId, Boolean isDeleted) {
        List<MemberProject> content = queryFactory.selectFrom(memberProject)
                .join(memberProject.project, project).fetchJoin()
                .leftJoin(project.cover, cover).fetchJoin()
                .where(memberProject.member.id.eq(memberId)
                        .and(memberProject.isStored.isFalse())
                        .and(isDeletedEq(isDeleted)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, content.stream().count());
    }

    public List<MemberProject> findMemberProject(Long memberId, Long projectId, Boolean isPinned, Boolean isStored, Boolean isDeleted) {
        List<MemberProject> content = queryFactory.selectFrom(memberProject)
                .join(memberProject.project, project).fetchJoin()
                .where(memberIdEq(memberId),
                        projectIdEq(projectId),
                        isPinnedEq(isPinned),
                        isStoredEq(isStored),
                        isDeletedEq(isDeleted))
                .fetch();

        return content;
    }

    public Optional<MemberProject> findMemberProjectByMemberIdAndProjectId(Long memberId, Long projectId, Boolean isDeleted) {
        Optional<MemberProject> content = queryFactory.selectFrom(memberProject)
                .join(memberProject.project, project).fetchJoin()
                .where(memberIdEq(memberId),
                        projectIdEq(projectId),
                        isDeletedEq(isDeleted))
                .stream().findAny();

        return content;
    }

    public Boolean existsMemberProjectByMemberIdAndProjectId(Long memberId, Long projectId, Boolean isDeleted) {
        Integer content = queryFactory.selectOne().from(memberProject)
                .where(memberProject.member.id.eq(memberId),
                        memberProject.project.id.eq(projectId),
                        isDeletedEq(isDeleted)).fetchFirst();

        return content != null;
    }

    private BooleanExpression isDeletedEq(Boolean isDeletedCond) {
        return isDeletedCond != null ? project.isDeleted.eq(isDeletedCond) : null;
    }

    private BooleanExpression isStoredEq(Boolean isStoredCond) {
        return isStoredCond != null ? memberProject.isStored.eq(isStoredCond) : null;
    }

    private BooleanExpression isPinnedEq(Boolean isPinnedCond) {
        return isPinnedCond != null ? memberProject.isPinned.eq(isPinnedCond) : null;
    }

    private BooleanExpression memberIdEq(Long memberIdCond) {
        return memberIdCond != null ? memberProject.member.id.eq(memberIdCond) : null;
    }

    private BooleanExpression projectIdEq(Long projectIdCond) {
        return projectIdCond != null ? memberProject.project.id.eq(projectIdCond) : null;
    }
}
