package com.datn.doffice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comment")
public class CommentEntity {
    @Id
    private String id;
    
    @Field("parent_id")
    private String parentId;

    @Field("text")
    private String text;

    @Field("user_id")
    private String userId;

    @Field("version_id")
    private String versionId;

    @Field("created_at")
    private Date createdAt;

    @Field("is_deleted")
    private boolean isDeleted;
}
