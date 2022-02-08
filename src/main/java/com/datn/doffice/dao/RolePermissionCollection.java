package com.datn.doffice.dao;

import com.datn.doffice.entity.RolePermissionEntity;
import com.datn.doffice.entity.UserRoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RolePermissionCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public List<?> findAllPermissionByRole(String roleId, Boolean isEntity) {
        List<RolePermissionEntity> listPermission = new ArrayList<>();
        List<String> listPermissionId = new ArrayList<>();
        Criteria criteria = Criteria.where("role_id").is(roleId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        listPermission = mongoTemplate.find(query, RolePermissionEntity.class);

        if (isEntity) {
            return listPermission;
        } else {
            for (RolePermissionEntity rolePermissionEntity : listPermission) {
                listPermissionId.add(rolePermissionEntity.getPermissionId());
            }
            return listPermissionId;
        }
    }

    public RolePermissionEntity deletePermissionOfRole(RolePermissionEntity rolePermissionEntity) {
        rolePermissionEntity.setIsDeleted(true);
        return mongoTemplate.save(rolePermissionEntity);
    }

    public List<RolePermissionEntity> findAllByPermissionId(String permissionId) {
        Criteria criteria = Criteria.where("permission_id").is(permissionId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        List<RolePermissionEntity> list = mongoTemplate.find(query, RolePermissionEntity.class);
        return list;
    }
}
