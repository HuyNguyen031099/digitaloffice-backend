package com.datn.doffice.controller;

import com.datn.doffice.dto.UserLoginDetailDTO;
import com.datn.doffice.entity.UserEntity;
import com.datn.doffice.enums.ApiError;
import com.datn.doffice.enums.ApiStatus;
import com.datn.doffice.utils.ApiMessage;
import com.datn.doffice.utils.ObjectMapperUtils;
import com.datn.doffice.utils.RequestUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class ApiController {
    public ApiController() {
        if (gson == null) {
            gson = new Gson();
        }
    }

    @Autowired
    private Gson gson;

    @Autowired
    private RequestUtil requestUtil;

    protected String getMessage(String code, HttpServletRequest request) {
        return requestUtil.getLocaleMessage(code, request);
    }

    protected ResponseEntity<String> ok(Object data) {
        ApiMessage apiMessage = ApiMessage
                .builder()
                .success(true)
                .code("200")
                .message("OK")
                .data(data)
                .build();

        return new ResponseEntity<>(gson.toJson(apiMessage), HttpStatus.OK);
    }

    protected ResponseEntity<String> ok(ApiStatus status, HttpServletRequest request) {
        ApiMessage apiMessage = ApiMessage.builder().success(true).code(status.getCode())
                .message(getMessage(status.getCode(), request)).build();
        return new ResponseEntity<>(gson.toJson(apiMessage), HttpStatus.OK);
    }

    protected ResponseEntity<String> error(ApiError error, HttpServletRequest request) {
        ApiMessage apiMessage = ApiMessage
                .builder()
                .code(error.getCode())
                .success(false)
                .message(getMessage(error.getCode(), request))
                .build();
        return new ResponseEntity<>(gson.toJson(apiMessage), error.getStatus());
    }

//    protected ResponseEntity<String> error(ApiError error, HttpServletRequest request, Object... params) {
//        ApiMessage apiMessage = ApiMessage
//                .builder()
//                .code(error.getCode())
//                .success(false)
//                .message(String.format(getMessage(error.getCode(), request), params))
//                .build();
//        return new ResponseEntity<>(apiMessage, request, error.getStatus());
//    }

//    protected ResponseEntity<String> error(ApiError error, Object data, HttpServletRequest request) {
//        ApiMessage apiMessage = ApiMessage
//                .builder()
//                .code(error.getCode())
//                .success(false)
//                .data(gson.toJson(data))
//                .message(getMessage(error.getCode(), request))
//                .build();
//        return new ResponseEntity<>(apiMessage, request, error.getStatus());
//    }

//    protected ResponseEntity<String> error(Object data, ApiError error, HttpServletRequest request) {
//        ApiMessage apiMessage = ApiMessage
//                .builder()
//                .success(false)
//                .code(error.getCode())
//                .message(gson.toJson(data))
//                .build();
//        return new ResponseEntity<>(apiMessage, request, error.getStatus());
//    }

    protected ResponseEntity<String> error(List<FieldError> error, HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<>();
        for (FieldError er : error) {
            if (map.containsKey(er.getField())) {
                continue;
            }
            map.put(er.getField(), er.getDefaultMessage());
        }
        ApiMessage apiMessage = ApiMessage
                .builder()
                .code(ApiError.MODEL_INVALID.getCode())
                .message(getMessage(ApiError.MODEL_INVALID.getCode(), request))
                .success(false)
                .data(gson.toJson(map)).build();
        return new ResponseEntity<>(gson.toJson(apiMessage), ApiError.MODEL_INVALID.getStatus());
    }

    protected <D, T> D mapper(final T entity, Class<D> outClass) {
        if (entity == null) {
            return null;
        }
        return ObjectMapperUtils.map(entity, outClass);
    }

    /**
     * Mapper.
     *
     * @param <D>        the generic type
     * @param <T>        the generic type
     * @param entityList the entity list
     * @param outCLass   the out C lass
     * @return the list
     */
    protected <D, T> List<D> mapper(final Collection<T> entityList, Class<D> outCLass) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return ObjectMapperUtils.mapAll(entityList, outCLass);
    }

    protected String getCurrentToken(HttpServletRequest request) {
        return requestUtil.getCurrentToken(request);
    }

    protected UserLoginDetailDTO getCurrentUser(HttpServletRequest request) {
        return requestUtil.getCurrentUser(request);
    }

    protected void logError(Exception ex) {
        if (log.isErrorEnabled()) {
            log.error(ex.getMessage(), ex);
        }
    }
}
