package com.datn.doffice.controller;

import com.datn.doffice.annotation.Api;
import com.datn.doffice.annotation.Rbac;
import com.datn.doffice.dto.*;
import com.datn.doffice.entity.AttachmentEntity;
import com.datn.doffice.entity.OfficialDispatchEntity;
import com.datn.doffice.enums.ApiError;
import com.datn.doffice.enums.ApiStatus;
import com.datn.doffice.service.OfficialDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Api(path = "/api/official-dispatch")
public class OfficialDispatchController extends ApiController {

    @Autowired
    private OfficialDispatchService officialDispatchService;

    /**
     *  Quản lý công văn đến
     */

    @PostMapping(value = "/coming-dispatch", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Rbac(roleTypes = {3}, permissionGroups = {4})
    public ResponseEntity<?> createDispatchByForm(@ModelAttribute ComingDispatchDTO comingDispatchDTO, HttpServletRequest request) {
        UserLoginDetailDTO curUser = getCurrentUser(request);
        try {
            officialDispatchService.createComingDispatch(comingDispatchDTO, curUser);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return error(ApiError.INTERNAL_SERVER_ERROR, request);
        }
        return ok(ApiStatus.OK);
    }

    @GetMapping("/coming-dispatch")
    public ResponseEntity<?> getAllComingDispatch() {
        List<OfficialDispatchEntity> result = officialDispatchService.getAllComingDispatch();
        return ok(result);
    }

    @GetMapping("/coming-dispatch/{comingDispatchId}")
    public ResponseEntity<?> getComingDispatchById(@PathVariable String comingDispatchId) {
        OfficialDispatchEntity result = officialDispatchService.getComingDispatchById(comingDispatchId);
        return ok(result);
    }

    @DeleteMapping ("/coming-dispatch/{comingDispatchId}")
    public ResponseEntity<?> deleteComingDispatch(@PathVariable String comingDispatchId) {
        OfficialDispatchEntity result = officialDispatchService.deleteComingDispatch(comingDispatchId);
        return ok(result);
    }

    @PostMapping("/coming-dispatch/forward")
    public ResponseEntity<?> forward(@RequestBody ForwardOfficialDispatchDTO forwardOfficialDispatchDTO, HttpServletRequest request) {
        UserLoginDetailDTO curUser = getCurrentUser(request);
        officialDispatchService.forward(forwardOfficialDispatchDTO, curUser);
        return ok(ApiStatus.OK);
    }

    @GetMapping("/coming-dispatch/approve/{officialDispatchId}")
    public ResponseEntity<?> approve(@PathVariable String officialDispatchId, HttpServletRequest request) {
        UserLoginDetailDTO curUser = getCurrentUser(request);
        officialDispatchService.approve(officialDispatchId, curUser);
        return ok(ApiStatus.OK);
    }

    @GetMapping("/coming-dispatch/get-number/{officialDispatchId}")
    public ResponseEntity<?> getNumber(@PathVariable String officialDispatchId, HttpServletRequest request) {
        UserLoginDetailDTO curUser = getCurrentUser(request);
        String documentNumber = officialDispatchService.getNumber(officialDispatchId, curUser);
        return ok(ApiStatus.OK);
    }

    @PutMapping("/coming-dispatch/update/{officialDispatchId}")
    public ResponseEntity<?> updateDispatch(@PathVariable String officialDispatchId, @ModelAttribute ComingDispatchDTO comingDispatchDTO, HttpServletRequest request) {
        UserLoginDetailDTO curUser = getCurrentUser(request);
        officialDispatchService.updateDispatch(officialDispatchId, comingDispatchDTO, curUser);
        return ok(ApiStatus.OK);
    }

    @GetMapping("/coming-dispatch/get-all-attachment-by-dispatch/{officialDispatchId}")
    public ResponseEntity<?> getAllAttachmentByDispatch(@PathVariable String officialDispatchId) {
        List<Resource> resources = officialDispatchService.getAllAttachmentByDispatch(officialDispatchId);
        return ok(resources);
    }

    @PostMapping("/coming-dispatch/add-viewer")
    public ResponseEntity<?> addViewerToDispatch(@RequestBody AddUserViewDispatchDTO addUserViewDispatchDTO, HttpServletRequest request) {
        UserLoginDetailDTO curUser = getCurrentUser(request);
        officialDispatchService.addViewerToDispatch(addUserViewDispatchDTO, curUser);
        return ok(ApiStatus.OK);
    }

    /**
     *  Quản lý công văn đi
     */

    @GetMapping("/out-going-dispatch")
    public ResponseEntity<?> getAllOutGoingDispatch() {
        List<OfficialDispatchEntity> result = officialDispatchService.getAllOutGoingDispatch();
        return ok(result);
    }

    @GetMapping("/out-going-dispatch/{outGoingDispatchId}")
    public ResponseEntity<?> getOutGoingDispatchById(@PathVariable String outGoingDispatchId) {
        OfficialDispatchEntity result = officialDispatchService.getOutGoingDispatchById(outGoingDispatchId);
        return ok(result);
    }

    /**
     * Tạo mới công văn đi. Người dùng phải có quyền nào đó mới được tạo. Ví dụ: chuyên viên đơn vị
     */
    @PostMapping(value = "/out-going-dispatch", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createOutGoingDispatchByForm(@ModelAttribute OutGoingDispatchDTO outGoingDispatchDTO, HttpServletRequest request) {
        UserLoginDetailDTO curUser = getCurrentUser(request);
        try {
            officialDispatchService.createOutGoingDispatch(outGoingDispatchDTO, curUser);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return error(ApiError.INTERNAL_SERVER_ERROR, request);
        }
        return ok(ApiStatus.OK);
    }

    @PostMapping(value = "/out-going-dispatch/submit-to-unit-leadership")
    public ResponseEntity<?> submitToUnitLeadership(@RequestBody SubmitToUnitLeadershipDTO submitToUnitLeadershipDTO, HttpServletRequest request) {
        UserLoginDetailDTO curUser = getCurrentUser(request);
        try {
            officialDispatchService.submitToUnitLeadership(submitToUnitLeadershipDTO, curUser);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return error(ApiError.INTERNAL_SERVER_ERROR, request);
        }
        return ok(ApiStatus.OK);
    }
}
