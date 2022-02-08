package com.datn.doffice.dao;

import com.datn.doffice.entity.UserViewTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserViewTypeCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public UserViewTypeEntity findByCode(String code) {
        Criteria criteria = Criteria.where("code").is(code);
        Query query = new Query(criteria);
        UserViewTypeEntity userViewTypeEntity = mongoTemplate.findOne(query, UserViewTypeEntity.class);
        return userViewTypeEntity;
    }


}
