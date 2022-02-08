package com.datn.doffice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
public class UserEntity {
    @Id
    private String id;

    @Field("full_name")
    private String fullName;

    @Field("email")
    private String email;

    @Field("phone")
    private String phone;

    @Field("user_name")
    private String userName;

    @Field("password")
    private String password;

    @Field("description")
    private String description;

    @Field("created_by")
    private String created_by;

    @Field("created_time")
    private Date createdTime;

    @Field("is_active")
    private Boolean isActive;

    @Field("is_deleted")
    private Boolean isDeleted;

    @Field("updated_by")
    private String updatedBy;

    @Field("updated_time")
    private Date updatedTime;

}
