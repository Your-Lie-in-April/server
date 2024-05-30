package com.appcenter.timepiece.generated.com.appcenter.timepiece.domain;

import com.appcenter.timepiece.domain.Cover;
import com.appcenter.timepiece.domain.Project;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QCover is a Querydsl query type for Cover
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCover extends EntityPathBase<Cover> {

    private static final long serialVersionUID = 2106440831L;

    public static final QCover cover = new QCover("cover");

    public final StringPath coverImageUrl = createString("coverImageUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Project, QProject> projects = this.<Project, QProject>createList("projects", Project.class, QProject.class, PathInits.DIRECT2);

    public QCover(String variable) {
        super(Cover.class, forVariable(variable));
    }

    public QCover(Path<? extends Cover> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCover(PathMetadata metadata) {
        super(Cover.class, metadata);
    }

}

