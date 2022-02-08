package com.datn.doffice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notification")
public class NotificationEntity {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("title")
    private String title;

    @Field("notification_type_id")
    private String notificationTypeId;

    @Field("created_at")
    private Date createdAt;

    @Field("is_deleted")
    private Boolean isDeleted;
}
