package com.datn.doffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForwardOfficialDispatchDTO {
    // Nguoi xu ly
    private String userId;

    // Cong van
    private String officialDispatchId;

    // Noi dung chuyen tiep
    private String content;
}
