package com.datn.doffice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * Thêm khi gọi API tác động đến công văn
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "activity_history")
public class ActivityHistoryEntity {
    @Id
    private String id;

    @Field("action_on_dispatch_id")
    private String actionId;

    @Field("user_id")
    private String userId;

    @Field("official_dispatch_id")
    private String officialDispatchId;

    @Field("meta_data")
    private Map<String, Object> metaData;

    @Field("created_at")
    private Date createdAt;

    @Field("is_deleted")
    private Boolean isDeleted;
}
