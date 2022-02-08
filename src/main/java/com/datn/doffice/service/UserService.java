package com.datn.doffice.service;

import com.datn.doffice.dto.*;
import com.datn.doffice.entity.*;

import java.util.List;

public interface UserService {

    /**
     * Thêm mới người dùng
     */
    void insertUser(UserDTO userDTO, UserLoginDetailDTO userLoginDetailDTO);

    void createRole(RoleDTO roleDTO);

    void createPermission(PermissionDTO permissionDTO);

    void addRoleForUser(UserRoleDTO userRoleDTO);

    void addPermissionToRole(RolePermissionDTO rolePermissionDTO);
    
    UserEntity getUserByUsername(String username);
    
    UserEntity getUserById(String userId);

    List<UserEntity> getAllUser();

    List<RoleEntity> getAllRole();

    List<PermissionEntity> getAllPermission();

    List<RoleEntity> getAllRoleOfUser(String userId);

    List<PermissionEntity> getAllPermissionOfRole(String roleId);

    UserEntity deleteUser(String userId);

    List<UserRoleEntity> deleteRoleOfUser(String userId, List<String> listRoleId);

    List<RolePermissionEntity> deletePermissionOfRole(String roleId, List<String> listPermissionId);

    RoleEntity deleteRole(String roleId);

    PermissionEntity deletePermission(String permissionId);
}
