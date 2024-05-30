package com.appcenter.timepiece.generated.com.appcenter.timepiece.domain;

import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Schedule;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QMemberProject is a Querydsl query type for MemberProject
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberProject extends EntityPathBase<MemberProject> {

    private static final long serialVersionUID = 1726035943L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberProject memberProject = new QMemberProject("memberProject");

    public final com.appcenter.timepiece.common.QBaseTimeEntity _super = new com.appcenter.timepiece.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isPinned = createBoolean("isPinned");

    public final BooleanPath isPrivileged = createBoolean("isPrivileged");

    public final BooleanPath isStored = createBoolean("isStored");

    public final QMember member;

    public final StringPath nickname = createString("nickname");

    public final QProject project;

    public final ListPath<Schedule, QSchedule> schedules = this.<Schedule, QSchedule>createList("schedules", Schedule.class, QSchedule.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberProject(String variable) {
        this(MemberProject.class, forVariable(variable), INITS);
    }

    public QMemberProject(Path<? extends MemberProject> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberProject(PathMetadata metadata, PathInits inits) {
        this(MemberProject.class, metadata, inits);
    }

    public QMemberProject(Class<? extends MemberProject> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
    }

}

