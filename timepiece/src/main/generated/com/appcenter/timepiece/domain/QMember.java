package com.appcenter.timepiece.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1151941330L;

    public static final QMember member = new QMember("member1");

    public final com.appcenter.timepiece.common.QBaseTimeEntity _super = new com.appcenter.timepiece.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<MemberProject, QMemberProject> memberProjects = this.<MemberProject, QMemberProject>createList("memberProjects", MemberProject.class, QMemberProject.class, PathInits.DIRECT2);

    public final StringPath nickname = createString("nickname");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath provider = createString("provider");

    public final ListPath<String, StringPath> role = this.<String, StringPath>createList("role", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath state = createString("state");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

