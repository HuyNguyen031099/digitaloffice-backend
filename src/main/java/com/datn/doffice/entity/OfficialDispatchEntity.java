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
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "official_dispatch")
@Builder
public class OfficialDispatchEntity {
    @Id
    private String id;

    @Field("is_coming_dispatch")
    private Boolean isComingDispatch;

    @Field("document_number")
    private String documentNumber;

    @Field("sign_by")
    private String signBy;

    @Field("sign_date")
    private Date signDate;

    @Field("arrival_date")
    private Date arrivalDate;

    @Field("document_type_id")
    private String documentTypeId;

    @Field("release_department_id")
    private String releaseDepartmentId;

    @Field("main_content")
    private String mainContent;

    @Field("total_page")
    private Integer totalPage;

    @Field("security_level")
    private Integer securityLevel;

    @Field("urgency_level")
    private Integer urgencyLevel;

    @Field("effective_date")
    private Date effectiveDate;

    @Field("expiration_date")
    private Date expirationDate;

    @Field("storage_location_id")
    private String storageLocationId;

    /**
     * 1: Chưa xử lý
     * 2: Đã xử lý
     */
    @Field("status")
    private Integer status;

    @Field("approve_date")
    private Date approveDate;

    @Field("receive_address")
    private String receiveAddress;

    @Field("created_by")
    private String createdBy;

    @Field("updated_by")
    private String updatedBy;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    @Field("is_deleted")
    private Boolean isDeleted;
}
