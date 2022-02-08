package com.datn.doffice.dao;

import com.datn.doffice.entity.ActionOnDispatchEntity;
import com.datn.doffice.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ActionOnDispatchCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public ActionOnDispatchEntity findByActionCode(Integer actionCode) {
        Criteria criteria = Criteria.where("action_code").is(actionCode);
        Query query = new Query(criteria);
        ActionOnDispatchEntity actionOnDispatchEntity = mongoTemplate.findOne(query, ActionOnDispatchEntity.class);
        return actionOnDispatchEntity;
    }
}
