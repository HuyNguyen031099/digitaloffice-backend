package com.datn.doffice.utils;

import com.datn.doffice.enums.ApiError;
import com.datn.doffice.exceptions.RequestNotFoundException;
import com.datn.doffice.exceptions.TokenExpiredException;
import com.datn.doffice.exceptions.UnauthorizedException;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@NoArgsConstructor
@Service
public class ResponseUtil {

    @Autowired
    private Gson gson;

    public void writeException(HttpServletResponse response, Exception exception, HandlerMethod handlerMethod) {

        try {
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpStatus.OK.value());

            ApiMessage msg = ApiMessage.builder().success(false).build();

            //Catch url invalid from client
            if (exception instanceof RequestNotFoundException
                    || exception instanceof HttpRequestMethodNotSupportedException) {
                msg.setCode(ApiError.REQUEST_NOT_FOUND.getCode());
                msg.setMessage("Yêu cầu không hợp lệ !");
                log.info("RequestNotFoundException");
            }

            //Catch AccessDeniedException
            if (exception instanceof AccessDeniedException) {
                msg.setCode(ApiError.ACCESS_DENIED.getCode());
                msg.setMessage("Bạn không đủ quyền trên chức năng này!");
                log.info("AccessDeniedException");
            }

            //Catch UnauthorizedException
            if (exception instanceof UnauthorizedException) {
                msg.setCode(ApiError.UNAUTHORIZED.getCode());
                msg.setMessage("Vui lòng đăng nhập.");
                log.info("UnauthorizedException");
            }

            //Catch TokenExpiredException
            if (exception instanceof TokenExpiredException) {
                msg.setCode(ApiError.TOKEN_EXPIRED.getCode());
                msg.setMessage("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại");
                log.info("TokenExpiredException");
            }

            //Write detail error to response
            response.getWriter().write(gson.toJson(msg));
            response.getWriter().close();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}

