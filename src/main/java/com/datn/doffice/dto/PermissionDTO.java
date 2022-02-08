package com.datn.doffice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {
    private Integer permissionCode;

    private String permissionName;

    private String description;
}
