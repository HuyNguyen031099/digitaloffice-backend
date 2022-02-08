package com.datn.doffice.dao;

import com.datn.doffice.entity.PermissionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PermissionCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public PermissionEntity findById(String permissionId) {
        Criteria criteria = Criteria.where("_id").is(permissionId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        PermissionEntity permissionEntity = mongoTemplate.findOne(query, PermissionEntity.class);
        return permissionEntity;
    }

    public PermissionEntity findByPermissionCode(Integer permissionCode) {
        Criteria criteria = Criteria.where("permission_code").is(permissionCode)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        PermissionEntity permissionEntity = mongoTemplate.findOne(query, PermissionEntity.class);
        return permissionEntity;
    }

    public PermissionEntity findByPermissionName(String permissionName) {
        Criteria criteria = Criteria.where("permission_name").is(permissionName)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        PermissionEntity permissionEntity = mongoTemplate.findOne(query, PermissionEntity.class);
        return permissionEntity;
    }

    public List<PermissionEntity> findAll() {
        Criteria criteria = Criteria.where("is_deleted").is(false);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, PermissionEntity.class);
    }

    public PermissionEntity deletePermission(String permissionId) {
        Criteria criteria = Criteria.where("_id").is(permissionId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        PermissionEntity permissionEntity = mongoTemplate.findOne(query, PermissionEntity.class);
        if (permissionEntity != null) {
            permissionEntity.setIsDeleted(true);
            PermissionEntity result = mongoTemplate.save(permissionEntity);
            return result;
        }
        return null;
    }
}
