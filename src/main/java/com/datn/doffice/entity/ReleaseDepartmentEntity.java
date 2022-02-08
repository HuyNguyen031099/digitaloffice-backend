package com.datn.doffice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "release_department")
public class ReleaseDepartmentEntity {
    @Id
    private String id;

    @Field("code")
    private String code;

    @Field("department_name")
    private String departmentName;

    @Field("note")
    private String note;
}
