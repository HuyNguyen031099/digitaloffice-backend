package com.datn.doffice.dao;

import com.datn.doffice.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public UserEntity findByUsername(String username) {
        Criteria criteria = Criteria.where("user_name").is(username)
                .and("is_active").is(true)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        UserEntity userEntity = mongoTemplate.findOne(query, UserEntity.class);
        return userEntity;
    }

    public UserEntity findById(String id) {
        Criteria criteria = Criteria.where("_id").is(id)
                .and("is_active").is(true)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        UserEntity userEntity = mongoTemplate.findOne(query, UserEntity.class);
        return userEntity;
    }

    public UserEntity findByEmail(String email) {
        Criteria criteria = Criteria.where("email").is(email)
                .and("is_active").is(true)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        UserEntity userEntity = mongoTemplate.findOne(query, UserEntity.class);
        return userEntity;
    }

    public List<UserEntity> findAll() {
        Criteria criteria = Criteria.where("is_active").is(true).and("is_deleted").is(false);
        Query query = new Query(criteria);
        List<UserEntity> listUser = mongoTemplate.find(query, UserEntity.class);
        return listUser;
    }

    public UserEntity deleteById(String userId) {
        Criteria criteria = Criteria.where("_id").is(userId);
        Query query = new Query(criteria);
        UserEntity userEntity = mongoTemplate.findOne(query, UserEntity.class);
        if (userEntity != null && userEntity.getIsActive() && !userEntity.getIsDeleted()) {
            userEntity.setIsActive(false);
            userEntity.setIsDeleted(true);
            mongoTemplate.save(userEntity);
        }
        return userEntity;
    }
}
