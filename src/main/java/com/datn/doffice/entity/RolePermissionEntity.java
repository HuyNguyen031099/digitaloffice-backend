package com.datn.doffice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "role_permission")
public class RolePermissionEntity {
    @Id
    private String id;

    @Field("role_id")
    private String roleId;

    @Field("permission_id")
    private String permissionId;

    @Field("created_at")
    private Date createdAt;

    @Field("is_deleted")
    private Boolean isDeleted;
}
