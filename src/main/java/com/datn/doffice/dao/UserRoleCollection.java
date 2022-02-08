package com.datn.doffice.dao;

import com.datn.doffice.entity.RoleEntity;
import com.datn.doffice.entity.UserEntity;
import com.datn.doffice.entity.UserRoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRoleCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public List<?> findAllRoleOfUser(String userId, Boolean isEntity) {
        List<UserRoleEntity> listRole = new ArrayList<>();
        List<String> listRoleId = new ArrayList<>();
        Criteria criteria = Criteria.where("user_id").is(userId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        listRole = mongoTemplate.find(query, UserRoleEntity.class);

        if (isEntity) {
            return listRole;
        } else{
            for (UserRoleEntity userRoleEntity : listRole) {
                listRoleId.add(userRoleEntity.getRoleId());
            }
            return listRoleId;
        }
    }

    public UserRoleEntity deleteRoleOfUser(UserRoleEntity userRoleEntity) {
        userRoleEntity.setIsDeleted(true);
        return mongoTemplate.save(userRoleEntity);
    }

    public List<UserRoleEntity> findAllByRoleId(String roleId) {
        Criteria criteria = Criteria.where("role_id").is(roleId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        List<UserRoleEntity> list = mongoTemplate.find(query, UserRoleEntity.class);
        return list;
    }

}
