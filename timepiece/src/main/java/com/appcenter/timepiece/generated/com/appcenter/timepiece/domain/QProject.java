package com.appcenter.timepiece.generated.com.appcenter.timepiece.domain;

import com.appcenter.timepiece.domain.Invitation;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Project;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 92250593L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProject project = new QProject("project");

    public final com.appcenter.timepiece.common.QBaseTimeEntity _super = new com.appcenter.timepiece.common.QBaseTimeEntity(this);

    public final StringPath color = createString("color");

    public final QCover cover;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final SetPath<java.time.DayOfWeek, EnumPath<java.time.DayOfWeek>> daysOfWeek = this.<java.time.DayOfWeek, EnumPath<java.time.DayOfWeek>>createSet("daysOfWeek", java.time.DayOfWeek.class, EnumPath.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> endTime = createTime("endTime", java.time.LocalTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Invitation, QInvitation> invitations = this.<Invitation, QInvitation>createList("invitations", Invitation.class, QInvitation.class, PathInits.DIRECT2);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final ListPath<MemberProject, QMemberProject> memberProjects = this.<MemberProject, QMemberProject>createList("memberProjects", MemberProject.class, QMemberProject.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QProject(String variable) {
        this(Project.class, forVariable(variable), INITS);
    }

    public QProject(Path<? extends Project> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProject(PathMetadata metadata, PathInits inits) {
        this(Project.class, metadata, inits);
    }

    public QProject(Class<? extends Project> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cover = inits.isInitialized("cover") ? new QCover(forProperty("cover")) : null;
    }

}

