package com.datn.doffice.dao;

import com.datn.doffice.entity.UserViewDispatchEntity;
import com.datn.doffice.entity.UserViewTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserViewDispatchCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public UserViewDispatchEntity findByUserIdAndDispatchId(String userId, String dispatchId) {
        Criteria criteria = Criteria.where("user_id").is(userId)
                .and("official_dispatch_id").is(dispatchId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        UserViewDispatchEntity userViewDispatchEntity = mongoTemplate.findOne(query, UserViewDispatchEntity.class);
        return userViewDispatchEntity;
    }

    public UserViewDispatchEntity updateObject(UserViewDispatchEntity userViewDispatchEntity) {
        return mongoTemplate.save(userViewDispatchEntity);
    }

    public UserViewDispatchEntity findByUserIdAndDispatchIdAndUserViewTypeId(String userId, String dispatchId, String userViewTypeId) {
        Criteria criteria = Criteria.where("user_id").is(userId)
                .and("official_dispatch_id").is(dispatchId)
                .and("user_view_type_id").is(userViewTypeId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, UserViewDispatchEntity.class);
    }
}
