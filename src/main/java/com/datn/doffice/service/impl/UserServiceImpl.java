package com.datn.doffice.service.impl;

import com.datn.doffice.dao.*;
import com.datn.doffice.dto.*;
import com.datn.doffice.entity.*;
import com.datn.doffice.exceptions.*;
import com.datn.doffice.service.UserService;
import com.datn.doffice.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserCollection userCollection;

    @Autowired
    private RoleCollection roleCollection;

    @Autowired
    private PermissionCollection permissionCollection;

    @Autowired
    private UserRoleCollection userRoleCollection;

    @Autowired
    private RolePermissionCollection rolePermissionCollection;
    
    @Autowired
    private UserSubscribeCollection userSubscribeCollection;
    
    @Autowired
    private DocumentCollection documentCollection;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void insertUser(UserDTO userDTO, UserLoginDetailDTO userLoginDetailDTO) {
        if (CommonUtils.isNullOrEmpty(userDTO.getFullName())) {
            throw new FullnameInvalidException();
        }

        if (!CommonUtils.isEmail(userDTO.getEmail())) {
            throw new EmailInvalidException();
        }

        UserEntity emailCheck = userCollection.findByEmail(userDTO.getEmail());
        if (emailCheck != null) {
            throw new EmailConflictException();
        }

        if (!CommonUtils.isPhoneNumber(userDTO.getPhone())) {
            throw new PhoneInvalidException();
        }

        if (CommonUtils.isNullOrEmpty(userDTO.getUserName())) {
            throw new UsernameInvalidException();
        }

        UserEntity usernameCheck = userCollection.findByUsername(userDTO.getUserName());
        if (usernameCheck != null) {
            throw new UsernameConflictException();
        }

        if (!CommonUtils.isValidPassword(userDTO.getPassword())) {
            throw new PasswordInvalidException();
        }

        UserEntity userEntity = UserEntity.builder()
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .userName(userDTO.getUserName())
                .password(CommonUtils.encryptPassword(userDTO.getPassword()))
                .description(userDTO.getDescription())
                .created_by(userLoginDetailDTO.getUserId())
                .createdTime(new Date())
                .isActive(true)
                .isDeleted(false)
                .updatedBy(null)
                .updatedTime(null)
                .build();
        userCollection.insertObject(userEntity);

    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void createRole(RoleDTO roleDTO) {
        Integer roleCode = roleDTO.getRoleCode();
        if (roleCode == null) {
            throw new RoleCodeInvalidException();
        }
        RoleEntity checkRoleCode = roleCollection.findByRoleCode(roleCode);
        if (checkRoleCode != null) {
            throw new RoleCodeConflictException();
        }

        String roleName = roleDTO.getRoleName();
        String prefix = "ROLE_";
        String roleNameSearch = prefix + roleName.toUpperCase();
        if (CommonUtils.isNullOrEmpty(roleName)) {
            throw new RoleNameInvalidException();
        }
        RoleEntity checkRoleName = roleCollection.findByRoleName(roleNameSearch);
        if (checkRoleName != null) {
            throw new RoleNameConflictException();
        }

        RoleEntity roleEntity = RoleEntity.builder()
                .roleCode(roleCode)
                .roleName(roleNameSearch)
                .createdAt(new Date())
                .isDeleted(false)
                .build();

        roleCollection.insertObject(roleEntity);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void createPermission(PermissionDTO permissionDTO) {
        Integer permissionCode = permissionDTO.getPermissionCode();
        if (permissionCode == null) {
            throw new PermissionCodeInvalidException();
        }
        PermissionEntity checkPermissionCode = permissionCollection.findByPermissionCode(permissionCode);
        if (checkPermissionCode != null) {
            throw new PermissionCodeConflictException();
        }

        String permissionName = permissionDTO.getPermissionName();
        String prefix = "OP_";
        String permissionNameSearch = prefix + permissionName.toUpperCase();
        if (CommonUtils.isNullOrEmpty(permissionName)) {
            throw new PermissionNameInvalidException();
        }
        PermissionEntity checkPermissionName = permissionCollection.findByPermissionName(permissionNameSearch);
        if (checkPermissionName != null) {
            throw new PermissionNameConflictException();
        }

        PermissionEntity permissionEntity = PermissionEntity.builder()
                .permissionCode(permissionCode)
                .permissionName(permissionNameSearch)
                .createdAt(new Date())
                .isDeleted(false)
                .build();

        permissionCollection.insertObject(permissionEntity);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void addRoleForUser(UserRoleDTO userRoleDTO) {
        String userId = userRoleDTO.getUserId();
        if (CommonUtils.isNullOrEmpty(userId)) {
            throw new UserIdInvalidException();
        }
        UserEntity userEntity = userCollection.findById(userId);
        if (userEntity == null) {
            throw new UserNotFoundException();
        }

        List<String> listRole = userRoleDTO.getListRole();
        if (listRole == null || listRole.isEmpty()) {
            throw new ListRoleInvalidException();
        }

        List<String> listRoleOfUser = (List<String>) userRoleCollection.findAllRoleOfUser(userId, false);

        for (String roleId : listRole) {
            if (!listRoleOfUser.contains(roleId)) {
                UserRoleEntity userRoleEntity = UserRoleEntity.builder()
                        .roleId(roleId)
                        .userId(userId)
                        .createdAt(new Date())
                        .isDeleted(false)
                        .build();
                userRoleCollection.insertObject(userRoleEntity);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void addPermissionToRole(RolePermissionDTO rolePermissionDTO) {
        String roleId = rolePermissionDTO.getRoleId();
        if (CommonUtils.isNullOrEmpty(roleId)) {
            throw new RoleIdInvalidException();
        }

        RoleEntity roleEntity = roleCollection.findById(roleId);
        if (roleEntity == null) {
            throw new RoleNotFoundException();
        }

        List<String> listPermission = rolePermissionDTO.getListPermission();
        if (listPermission == null || listPermission.isEmpty()) {
            throw new ListPermissionInvalidException();
        }

        List<String> listPermissionOfRole = (List<String>) rolePermissionCollection.findAllPermissionByRole(roleId, false);

        for (String permissionId : listPermission) {
            if (!listPermissionOfRole.contains(permissionId)) {
                RolePermissionEntity rolePermissionEntity = RolePermissionEntity.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .createdAt(new Date())
                        .isDeleted(false)
                        .build();
                rolePermissionCollection.insertObject(rolePermissionEntity);
            }
        }
    }

    @Override
    public List<UserEntity> getAllUser() {
        List<UserEntity> list = userCollection.findAll();
        return list;
    }

    @Override
    public List<RoleEntity> getAllRole() {
        List<RoleEntity> list = roleCollection.findAll();
        return list;
    }

    @Override
    public List<PermissionEntity> getAllPermission() {
        List<PermissionEntity> list = permissionCollection.findAll();
        return list;
    }

    @Override
    public List<RoleEntity> getAllRoleOfUser(String userId) {
        List<UserRoleEntity> list = (List<UserRoleEntity>) userRoleCollection.findAllRoleOfUser(userId, true);
        List<RoleEntity> listRoleOfUser = new ArrayList<>();
        for (UserRoleEntity item : list) {
            RoleEntity role = roleCollection.findById(item.getRoleId());
            listRoleOfUser.add(role);
        }
        return listRoleOfUser;
    }

    @Override
    public List<PermissionEntity> getAllPermissionOfRole(String roleId) {
        List<RolePermissionEntity> list = (List<RolePermissionEntity>) rolePermissionCollection.findAllPermissionByRole(roleId, true);
        List<PermissionEntity> listPerOfRole = new ArrayList<>();
        for (RolePermissionEntity item : list) {
            PermissionEntity permissionEntity = permissionCollection.findById(item.getPermissionId());
            listPerOfRole.add(permissionEntity);
        }
        return listPerOfRole;
    }

    @Override
    public UserEntity deleteUser(String userId) {
        if (CommonUtils.isNullOrEmpty(userId)) {
            throw new UserIdInvalidException();
        }
        return userCollection.deleteById(userId);
    }

    @Override
    public List<UserRoleEntity> deleteRoleOfUser(String userId, List<String> listRoleId) {
        if (CommonUtils.isNullOrEmpty(userId)) {
            throw new UserIdInvalidException();
        }

        if (listRoleId == null || listRoleId.isEmpty()) {
            throw new ListRoleInvalidException();
        }
        List<UserRoleEntity> result = new ArrayList<>();

        // O(n^2)
        List<UserRoleEntity> listRoleOfUser = (List<UserRoleEntity>) userRoleCollection.findAllRoleOfUser(userId, true);
        if (listRoleOfUser != null && !listRoleOfUser.isEmpty()) {
            for (String roleId : listRoleId) {
                for (UserRoleEntity userRoleEntity : listRoleOfUser) {
                    if (userRoleEntity.getRoleId().equals(roleId)) {
                        UserRoleEntity ure = userRoleCollection.deleteRoleOfUser(userRoleEntity);
                        result.add(ure);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<RolePermissionEntity> deletePermissionOfRole(String roleId, List<String> listPermissionId) {
        if (CommonUtils.isNullOrEmpty(roleId)) {
            throw new RoleIdInvalidException();
        }

        if (listPermissionId == null || listPermissionId.isEmpty()) {
            throw new ListPermissionInvalidException();
        }
        List<RolePermissionEntity> result = new ArrayList<>();

        List<RolePermissionEntity> listRolePermission = (List<RolePermissionEntity>) rolePermissionCollection.findAllPermissionByRole(roleId, true);

        if (listRolePermission != null && !listRolePermission.isEmpty()) {
            for (String permissionId : listPermissionId) {
                for (RolePermissionEntity rolePermissionEntity : listRolePermission) {
                    if (rolePermissionEntity.getPermissionId().equals(permissionId)) {
                        RolePermissionEntity rpe = rolePermissionCollection.deletePermissionOfRole(rolePermissionEntity);
                        result.add(rpe);
                    }
                }
            }
        }
        return result;
    }


    @Override
    public RoleEntity deleteRole(String roleId) {
        if (CommonUtils.isNullOrEmpty(roleId)) {
            throw new RoleIdInvalidException();
        }

        List<UserRoleEntity> listUserRole = userRoleCollection.findAllByRoleId(roleId);
        List<RolePermissionEntity> listRolePermission = (List<RolePermissionEntity>) rolePermissionCollection.findAllPermissionByRole(roleId, true);
        if ((listUserRole == null || listUserRole.isEmpty()) && (listRolePermission == null || listRolePermission.isEmpty())) {
            RoleEntity roleEntity = roleCollection.deleteRole(roleId);
            return roleEntity;
        }
        return null;
    }

    @Override
    public PermissionEntity deletePermission(String permissionId) {
        if (CommonUtils.isNullOrEmpty(permissionId)) {
            throw new PermissionIdInvalidException();
        }

        List<RolePermissionEntity> list = rolePermissionCollection.findAllByPermissionId(permissionId);
        if (list == null || list.isEmpty()) {
            PermissionEntity permissionEntity = permissionCollection.deletePermission(permissionId);
            return permissionEntity;
        }
        return null;
    }

	@Override
	public UserEntity getUserByUsername(String username) {
		UserEntity user = userCollection.findByUsername(username);
		return user;
	}

	@Override
	public UserEntity getUserById(String userId) {
		return userCollection.findById(userId);
	}
	
}