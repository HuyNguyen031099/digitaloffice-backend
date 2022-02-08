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
@Document(collection = "user_view_dispatch")
public class UserViewDispatchEntity {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("official_dispatch_id")
    private String officialDispatchId;

    @Field("user_view_type_id")
    private String userViewTypeId;

    @Field("is_deleted")
    private Boolean isDeleted;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;
}
