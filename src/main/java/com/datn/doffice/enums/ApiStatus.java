package com.datn.doffice.enums;

import lombok.Getter;

@Getter
public enum ApiStatus {
  OK("OK");

  private String code;

  ApiStatus(String code) {
    this.code = code;
  }
}
