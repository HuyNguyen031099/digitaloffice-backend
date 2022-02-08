package com.datn.doffice.controller;

import com.datn.doffice.annotation.Api;
import com.datn.doffice.annotation.Rbac;
import com.datn.doffice.dto.*;
import com.datn.doffice.entity.*;
import com.datn.doffice.enums.ApiError;
import com.datn.doffice.enums.ApiStatus;
import com.datn.doffice.exceptions.*;
import com.datn.doffice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Api(path = "/api/admin")
public class UserController extends ApiController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        ResponseEntity<String> response;
        try {
            UserLoginDetailDTO userLoginDetailDTO = getCurrentUser(request);
            userService.insertUser(userDTO, userLoginDetailDTO);
            return ok(ApiStatus.OK);
        } catch (FullnameInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.FULLNAME_INVALID, request);
        } catch (EmailInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.EMAIL_INVALID, request);
        } catch (EmailConflictException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.EMAIL_CONFLICT, request);
        } catch (PhoneInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.PHONE_INVALID, request);
        } catch (UsernameInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.USERNAME_INVALID, request);
        } catch (UsernameConflictException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.USERNAME_CONFLICT, request);
        } catch (PasswordInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.PASSWORD_INVALID, request);
        }
        return response;
    }

    @PostMapping("/role")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO, HttpServletRequest request) {
        ResponseEntity<String> response;
        try {
            userService.createRole(roleDTO);
            return ok(ApiStatus.OK);
        } catch (RoleCodeInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.ROLE_CODE_INVALID, request);
        } catch (RoleCodeConflictException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.ROLE_CODE_CONFLICT, request);
        } catch (RoleNameInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.ROLE_NAME_INVALID, request);
        } catch (RoleNameConflictException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.ROLE_NAME_CONFLICT, request);
        }
        return response;
    }

    @PostMapping("/permission")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> createPermission(@RequestBody PermissionDTO permissionDTO, HttpServletRequest request) {
        ResponseEntity<String> response;
        try {
            userService.createPermission(permissionDTO);
            return ok(ApiStatus.OK);
        } catch (PermissionCodeInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.PERMISSION_CODE_INVALID, request);
        } catch (PermissionCodeConflictException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.PERMISSION_CODE_CONFLICT, request);
        } catch (PermissionNameInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.PERMISSION_NAME_INVALID, request);
        } catch (PermissionNameConflictException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.PERMISSION_NAME_CONFLICT, request);
        }
        return response;
    }

    @PostMapping("/add-role-user")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> addRoleForUser(@RequestBody UserRoleDTO userRoleDTO, HttpServletRequest request) {
        ResponseEntity<String> response;
        try {
            userService.addRoleForUser(userRoleDTO);
            return ok(ApiStatus.OK);
        } catch (UserIdInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.USER_ID_INVALID, request);
        } catch (UserNotFoundException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.USER_NOT_FOUND, request);
        } catch (ListRoleInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.LIST_ROLE_INVALID, request);
        }
        return response;
    }

    @PostMapping("/add-permission-role")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> addPermissionForRole(@RequestBody RolePermissionDTO rolePermissionDTO, HttpServletRequest request) {
        ResponseEntity<String> response;
        try {
            userService.addPermissionToRole(rolePermissionDTO);
            return ok(ApiStatus.OK);
        } catch (RoleIdInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.ROLE_ID_INVALID, request);
        } catch (RoleNotFoundException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.ROLE_NOT_FOUND, request);
        } catch (ListPermissionInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.LIST_PERMISSION_INVALID, request);
        }
        return response;
    }

    @GetMapping("/user")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> getAllUser() {
        List<UserEntity> list = userService.getAllUser();
        return ok(list);
    }

    @GetMapping("/role")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> getAllRole() {
        List<RoleEntity> list = userService.getAllRole();
        return ok(list);
    }

    @GetMapping("/permission")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> getAllPermission() {
        List<PermissionEntity> list = userService.getAllPermission();
        return ok(list);
    }

    @GetMapping("/user-role")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> getAllRoleOfUser(@RequestParam(value = "userId") String userId) {
        List<RoleEntity> list = userService.getAllRoleOfUser(userId);
        return ok(list);
    }

    @GetMapping("/permission-role")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> getAllPermissionOfRole(@RequestParam(value = "roleId") String roleId) {
        List<PermissionEntity> list = userService.getAllPermissionOfRole(roleId);
        return ok(list);
    }

    @DeleteMapping("/user")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> deleteUser(@RequestParam(value = "userId") String userId, HttpServletRequest request) {
        ResponseEntity<String> response;
        try {
            UserEntity userEntity = userService.deleteUser(userId);
            return ok(userEntity);
        } catch (UserIdInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.USER_ID_INVALID, request);
        }
        return response;
    }

    @DeleteMapping("/role-user")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> deleteRoleOfUser(@RequestParam(value = "userId") String userId,
                                              @RequestParam(value = "listRoleId") List<String> listRoleId,
                                              HttpServletRequest request
    ) {
        ResponseEntity<String> response;
        try {
            List<UserRoleEntity> result = userService.deleteRoleOfUser(userId, listRoleId);
            return ok(result);
        } catch (UserIdInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.USER_ID_INVALID, request);
        } catch (ListRoleInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.LIST_ROLE_INVALID, request);
        }
        return response;
    }

    @DeleteMapping("/permission-role")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> deletePermissionOfRole(@RequestParam(value = "roleId") String roleId,
                                                    @RequestParam(value = "listPermissionId") List<String> listPermissionId,
                                                    HttpServletRequest request
    ) {
        ResponseEntity<String> response;
        try {
            List<RolePermissionEntity> result = userService.deletePermissionOfRole(roleId, listPermissionId);
            return ok(result);
        } catch (RoleIdInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.ROLE_ID_INVALID, request);
        } catch (ListPermissionInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.LIST_PERMISSION_INVALID, request);
        }
        return response;
    }

    @DeleteMapping("/role")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> deleteRole(@RequestParam(value = "roleId") String roleId,
                                        HttpServletRequest request
    ) {
        ResponseEntity<String> response;
        try {
            RoleEntity result = userService.deleteRole(roleId);
            return ok(result);
        } catch (RoleIdInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.ROLE_ID_INVALID, request);
        }
        return response;
    }

    @DeleteMapping("/permission")
    @Rbac(roleTypes = {5}, isPrivate = true)
    public ResponseEntity<?> deletePermission(@RequestParam(value = "permissionId") String permissionId,
                                        HttpServletRequest request
    ) {
        ResponseEntity<String> response;
        try {
            PermissionEntity result = userService.deletePermission(permissionId);
            return ok(result);
        } catch (PermissionIdInvalidException ex) {
            log.error(ex.getMessage(), ex);
            response = error(ApiError.PERMISSION_ID_INVALID, request);
        }
        return response;
    }

}