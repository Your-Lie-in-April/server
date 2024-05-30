package com.appcenter.timepiece.generated.com.appcenter.timepiece.domain;

import com.appcenter.timepiece.domain.Invitation;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QInvitation is a Querydsl query type for Invitation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInvitation extends EntityPathBase<Invitation> {

    private static final long serialVersionUID = -1671214127L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInvitation invitation = new QInvitation("invitation");

    public final com.appcenter.timepiece.common.QBaseTimeEntity _super = new com.appcenter.timepiece.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProject project;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath url = createString("url");

    public QInvitation(String variable) {
        this(Invitation.class, forVariable(variable), INITS);
    }

    public QInvitation(Path<? extends Invitation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInvitation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInvitation(PathMetadata metadata, PathInits inits) {
        this(Invitation.class, metadata, inits);
    }

    public QInvitation(Class<? extends Invitation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
    }

}

