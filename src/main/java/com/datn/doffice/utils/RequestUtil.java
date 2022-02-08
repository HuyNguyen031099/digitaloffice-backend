package com.datn.doffice.utils;

import com.datn.doffice.config.security.JwtUtils;
import com.datn.doffice.config.security.MyUserPrincipal;
import com.datn.doffice.dto.UserLoginDetailDTO;
import com.datn.doffice.enums.ApiError;
import com.datn.doffice.enums.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class RequestUtil {

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private JwtUtils jwtUtils;

  private static final int TOKEN_START_INDEX = 7;
  private static final String LOCALHOST_IPV4 = "127.0.0.1";
  private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

  private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

  public String getLocaleMessage(String code, HttpServletRequest request) {
    return messageSource.getMessage(code, null, null);
  }

  public String getLocaleMessage(ApiStatus status, HttpServletRequest request) {
    return messageSource.getMessage(status.getCode(), null, null);
  }

  public String getLocaleMessage(ApiError error, HttpServletRequest request) {
    return messageSource.getMessage(error.getCode(), null, null);
  }

  public String getCurrentToken(HttpServletRequest request) {
    final String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(TOKEN_START_INDEX);
    }
    return null;
  }


  public UserLoginDetailDTO getCurrentUser(HttpServletRequest request) {
    final String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      String jwtToken = bearerToken.substring(TOKEN_START_INDEX);
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      MyUserPrincipal myUserPrincipal = (MyUserPrincipal) authentication.getPrincipal();
      String ipIncome = getClientIp(request);
      UserLoginDetailDTO userLoginDetailDTO = UserLoginDetailDTO.builder()
              .userId(myUserPrincipal.getUser().getId())
              .ipAddr(ipIncome)
              .expiredDate(jwtUtils.getExpirationDateFromToken(jwtToken))
              .roles(myUserPrincipal.getRoles())
              .permissions(myUserPrincipal.getPermissions())
              .build();
      return userLoginDetailDTO;
    }
    return null;
  }


  public static String getClientIp(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-Forwarded-For");
    if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }

    if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }

    if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
      if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
        try {
          InetAddress inetAddress = InetAddress.getLocalHost();
          ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
          logger.error(e.getMessage(), e);
        }
      }
    }

    if (!StringUtils.isEmpty(ipAddress)
            && ipAddress.length() > 15
            && ipAddress.indexOf(",") > 0) {
      ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
    }

    return ipAddress;
  }

}
