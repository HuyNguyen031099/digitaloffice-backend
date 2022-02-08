package com.datn.doffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserViewDispatchDTO {
    private String officialDispatchId;

    private String userId;
}
