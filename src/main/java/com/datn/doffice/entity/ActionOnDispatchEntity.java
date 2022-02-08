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
@Document(collection = "action_on_dispatch")
public class ActionOnDispatchEntity {
    @Id
    private String id;

    @Field("action_code")
    private Integer actionCode;

    @Field("action_name")
    private String actionName;
}
