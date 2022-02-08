package com.datn.doffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String fullName;

    private String email;

    private String phone;

    private String userName;

    private String password;

    private String description;

    private String created_by;

    private Date createdTime;

    private Boolean isActive;

    private Boolean isDeleted;

    private String updatedBy;

    private Date updatedTime;
}