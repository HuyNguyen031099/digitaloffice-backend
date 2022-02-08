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
@Document(collection = "attachment")
public class AttachmentEntity {
    @Id
    private String id;

    @Field("official_dispatch_id")
    private String officialDispatchId;

    @Field("file_name")
    private String fileName;

    @Field("file_size")
    private Long fileSize;

    @Field("file_type")
    private String fileType;

    @Field("url")
    private String url;

    @Field("created_at")
    private Date createdAt;

    @Field("is_deleted")
    private Boolean isDeleted;
}
