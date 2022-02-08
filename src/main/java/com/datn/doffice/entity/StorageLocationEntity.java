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
@Document(collection = "storage_location")
public class StorageLocationEntity {
    @Id
    private String id;

    @Field("location_name")
    private String locationName;

    @Field("note")
    private String note;
}
