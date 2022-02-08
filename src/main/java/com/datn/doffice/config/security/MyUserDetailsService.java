package com.datn.doffice.config.security;

import com.datn.doffice.dao.*;
import com.datn.doffice.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserCollection userCollection;

    @Autowired
    RoleCollection roleCollection;

    @Autowired
    UserRoleCollection userRoleCollection;

    @Autowired
    RolePermissionCollection rolePermissionCollection;

    @Autowired
    PermissionCollection permissionCollection;

    @Override
    public MyUserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userCollection.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy username: " + username);
        }
        List<UserRoleEntity> listRole = (List<UserRoleEntity>) userRoleCollection.findAllRoleOfUser(user.getId(), true);
        List<Integer> listRole1 = new ArrayList<>();
        for (UserRoleEntity role : listRole) {
            String roleId = role.getRoleId();
            RoleEntity roleEntity = null;
            roleEntity = roleCollection.findById(roleId);  // Hơi tốn tài nguyên
            listRole1.add(roleEntity.getRoleCode());
        }

        Set<Integer> permissions = new HashSet<>();
        for (UserRoleEntity userRoleEntity : listRole) {
            List<RolePermissionEntity> listPermission = (List<RolePermissionEntity>) rolePermissionCollection.findAllPermissionByRole(userRoleEntity.getRoleId(), true);
            for (RolePermissionEntity permission : listPermission) {
                String permissionId = permission.getPermissionId();
                PermissionEntity permissionEntity = permissionCollection.findById(permissionId);
                permissions.add(permissionEntity.getPermissionCode());
            }
        }
        return MyUserPrincipal.create(user, listRole1, permissions);
    }
}
