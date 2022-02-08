package com.datn.doffice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *  Bảng: Loại người xem công văn. Dùng để phân biệt người xem công văn thuộc loại nào.
 *  Ví dụ:
 *      + Người xem     - code: NX
 *      + Người xử lí   - code: NXL
 *      + Người nhận    - code: NN
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_view_type")
public class UserViewTypeEntity {
    @Id
    private String id;

    @Field("code")
    private String code;

    @Field("view_type")
    private String viewType;
}
