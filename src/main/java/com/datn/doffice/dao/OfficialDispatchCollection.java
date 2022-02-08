package com.datn.doffice.dao;

import com.datn.doffice.entity.OfficialDispatchEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OfficialDispatchCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public List<OfficialDispatchEntity> getAllComingDispatch() {
        Criteria criteria = Criteria.where("is_coming_dispatch").is(true)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        List<OfficialDispatchEntity> list = mongoTemplate.find(query, OfficialDispatchEntity.class);
        return list;
    }

    public List<OfficialDispatchEntity> getAllOutGoingDispatch() {
        Criteria criteria = Criteria.where("is_coming_dispatch").is(false)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        List<OfficialDispatchEntity> list = mongoTemplate.find(query, OfficialDispatchEntity.class);
        return list;
    }

    public OfficialDispatchEntity findComingDispatchById(String id) {
        Criteria criteria = Criteria.where("_id").is(id)
                .and("is_coming_dispatch").is(true)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        OfficialDispatchEntity officialDispatchEntity = mongoTemplate.findOne(query, OfficialDispatchEntity.class);
        return officialDispatchEntity;
    }

    public OfficialDispatchEntity findOutGoingDispatchById(String id) {
        Criteria criteria = Criteria.where("_id").is(id)
                .and("is_coming_dispatch").is(false)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        OfficialDispatchEntity officialDispatchEntity = mongoTemplate.findOne(query, OfficialDispatchEntity.class);
        return officialDispatchEntity;
    }

    public OfficialDispatchEntity deleteComingDispatch(String id) {
        Criteria criteria = Criteria.where("_id").is(id)
                .and("is_coming_dispatch").is(true)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        OfficialDispatchEntity officialDispatchEntity = mongoTemplate.findOne(query, OfficialDispatchEntity.class);
        officialDispatchEntity.setIsDeleted(true);
        return mongoTemplate.save(officialDispatchEntity);
    }

    public OfficialDispatchEntity updateObject(OfficialDispatchEntity officialDispatchEntity) {
        return mongoTemplate.save(officialDispatchEntity);
    }
}
