package com.appcenter.timepiece.repository.impl;

import com.appcenter.timepiece.domain.Project;
import com.appcenter.timepiece.domain.QMember;
import com.appcenter.timepiece.domain.QMemberProject;
import com.appcenter.timepiece.domain.QProject;
import com.appcenter.timepiece.repository.customRepository.ProjectRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private QProject project = QProject.project;
    private QMember member = QMember.member;
    private QMemberProject memberProject = QMemberProject.memberProject;

    @Override
    public Page<Project> searchProject(Long memberId, String keyword, Boolean isStored, Pageable pageable) {
        return findProject(memberId, keyword, isStored, false, pageable);
    }

    @Override
    public Page<Project> findProjectIsStored(Long memberId, Pageable pageable) {
        return findProject(memberId, null, true, false, pageable);
    }

    public Page<Project> findProject(Long memberId, String keyword, Boolean isStored, Boolean isDeleted, Pageable pageable) {
        List<Project> content = queryFactory
                .selectDistinct(project)
                .from(project)
                .join(project.memberProjects, memberProject)
                .join(memberProject.member, member)
                .where(member.id.eq(memberId),
                        isStoredEq(isStored),
                        isDeletedEq(isDeleted),
                        keywordEq(keyword))
                .fetch();

        return new PageImpl<>(content, pageable, content.stream().count());
    }

    private BooleanExpression isDeletedEq(Boolean isDeletedCond) {
        return isDeletedCond != null ? project.isDeleted.eq(isDeletedCond) : null;
    }

    private BooleanExpression isStoredEq(Boolean isStoredCond) {
        return isStoredCond != null ? memberProject.isStored.eq(isStoredCond) : null;
    }

    private BooleanExpression keywordEq(String keywordCond) {
        return keywordCond != null ? project.title.containsIgnoreCase(keywordCond) : null;
    }
}
