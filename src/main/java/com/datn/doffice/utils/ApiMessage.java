package com.datn.doffice.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiMessage implements Serializable {

    private static final long serialVersionUID = 61269086266661427L;

    private String code;

    private String title;

    private String message;

    private Object data;

    private boolean success;
}
