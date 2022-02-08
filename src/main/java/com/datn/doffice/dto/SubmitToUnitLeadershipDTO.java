package com.datn.doffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitToUnitLeadershipDTO {
    // cong van nao
    private String officialDispatchId;

    // lanh dao don vi
    private String userId;

}
