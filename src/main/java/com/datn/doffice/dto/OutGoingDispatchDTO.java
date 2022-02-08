package com.datn.doffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutGoingDispatchDTO {
//    @NotEmpty
//    private String documentNumber;

    @NotEmpty
    private String receiveAddress;

    // Van ban den thi rat de hieu
    // Van ban di thi...
    @NotEmpty
    private String releaseDepartmentId;

//    @NotEmpty
//    private String signBy;

//    @NotEmpty
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    private Date signDate;

//    @NotEmpty
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    private Date arrivalDate;

    @NotEmpty
    private String documentTypeId;

    @NotEmpty
    private String mainContent;

    @NotEmpty
    private Integer totalPage;

    @NotEmpty
    private Integer securityLevel;

    @NotEmpty
    private Integer urgencyLevel;

    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date effectiveDate;

    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date expirationDate;

//    @NotEmpty
//    private String storageLocationId;

//    @NotEmpty
//    private String receiverId;

    @NotNull(message = "Attachments is not valid")
    private List<MultipartFile> attachments;
}
