package com.datn.doffice.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiError {

    UNAUTHORIZED("0x00100", HttpStatus.OK),

    ACCESS_DENIED("0x00101", HttpStatus.OK),

    REQUEST_NOT_FOUND("0x00102", HttpStatus.OK),

    HTTP_BAD_METHOD("0x00103", HttpStatus.OK),

    BAD_REQUEST("0x00104", HttpStatus.OK),

    TOKEN_EXPIRED("0x00105", HttpStatus.OK),

    INTERNAL_SERVER_ERROR("0x00106", HttpStatus.OK),

    MODEL_INVALID("0x00107", HttpStatus.OK),

    PERMISSION_NOT_FOUND("0x00108", HttpStatus.OK),


    // Create new user
    FULLNAME_INVALID("0x00109", HttpStatus.OK),

    EMAIL_INVALID("0x00110", HttpStatus.OK),

    EMAIL_CONFLICT("0x00111", HttpStatus.OK),

    PHONE_INVALID("0x00112", HttpStatus.OK),

    USERNAME_INVALID("0x00113", HttpStatus.OK),

    USERNAME_CONFLICT("0x00114", HttpStatus.OK),

    PASSWORD_INVALID("0x00115", HttpStatus.OK),

    // Create new role
    ROLE_CODE_INVALID("0x00116", HttpStatus.OK),

    ROLE_CODE_CONFLICT("0x00117", HttpStatus.OK),

    ROLE_NAME_INVALID("0x00118", HttpStatus.OK),

    ROLE_NAME_CONFLICT("0x00119", HttpStatus.OK),

    // Create new permission

    PERMISSION_CODE_INVALID("0x00120", HttpStatus.OK),

    PERMISSION_CODE_CONFLICT("0x00121", HttpStatus.OK),

    PERMISSION_NAME_INVALID("0x00122", HttpStatus.OK),

    PERMISSION_NAME_CONFLICT("0x00123", HttpStatus.OK),


    // Add role for user
    USER_ID_INVALID("0x00124", HttpStatus.OK),

    USER_NOT_FOUND("0x00125", HttpStatus.OK),

    LIST_ROLE_INVALID("0x00126", HttpStatus.OK),

    // Add permission to role

    ROLE_ID_INVALID("0x00127", HttpStatus.OK),

    ROLE_NOT_FOUND("0x00128", HttpStatus.OK),

    LIST_PERMISSION_INVALID("0x00129", HttpStatus.OK),


    PERMISSION_ID_INVALID("0x00130", HttpStatus.OK),

    // upload
	FILE_EXIST("0x00131", HttpStatus.OK);

    private HttpStatus status;

    private String code;

    ApiError(String code, HttpStatus status) {
        this.status = status;
        this.code = code;
    }

}
