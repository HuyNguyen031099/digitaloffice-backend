
package com.datn.doffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDetailDTO {

  private String userId;

  private List<Integer> roles;

  private Set<Integer> permissions;

  private Date expiredDate;

  private String ipAddr;
}
