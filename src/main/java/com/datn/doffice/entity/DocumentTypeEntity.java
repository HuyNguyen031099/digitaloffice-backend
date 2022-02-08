package com.datn.doffice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "document_type")
public class DocumentTypeEntity {
    @Id
    private String id;

    @Field("type_name")
    private String typeName;

    // Code-Số thứ tự-
    @Field("type_code")
    private String typeCode;
}
