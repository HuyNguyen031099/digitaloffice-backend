package com.datn.doffice.dao;

import com.datn.doffice.entity.RoleEntity;
import com.datn.doffice.entity.UserRoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public RoleEntity findById(String roleId) {
        RoleEntity role = mongoTemplate.findById(roleId, RoleEntity.class);
        return role;
    }

    public RoleEntity findByRoleCode(Integer roleCode) {
        Criteria criteria = Criteria.where("role_code").is(roleCode)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        RoleEntity roleEntity = mongoTemplate.findOne(query, RoleEntity.class);
        return roleEntity;
    }

    public RoleEntity findByRoleName(String roleName) {
        Criteria criteria = Criteria.where("role_name").is(roleName)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        RoleEntity roleEntity = mongoTemplate.findOne(query, RoleEntity.class);
        return roleEntity;
    }

    public List<RoleEntity> findAll() {
        Criteria criteria = Criteria.where("is_deleted").is(false);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, RoleEntity.class);
    }

    public RoleEntity deleteRole(String roleId) {
        Criteria criteria = Criteria.where("_id").is(roleId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        RoleEntity roleEntity = mongoTemplate.findOne(query, RoleEntity.class);
        if (roleEntity != null) {
            roleEntity.setIsDeleted(true);
            return roleEntity;
        }
        return null;
    }
}
