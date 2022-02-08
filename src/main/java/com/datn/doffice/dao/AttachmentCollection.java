package com.datn.doffice.dao;

import com.datn.doffice.entity.AttachmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AttachmentCollection {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertObject(Object object) {
        mongoTemplate.insert(object);
    }

    public List<AttachmentEntity> findAllAttachmentByDispatchId(String dispatchId) {
        Criteria criteria = Criteria.where("official_dispatch_id").is(dispatchId)
                .and("is_deleted").is(false);
        Query query = new Query(criteria);
        List<AttachmentEntity> list = mongoTemplate.find(query, AttachmentEntity.class);
        return list;
    }

    public AttachmentEntity updateObject(AttachmentEntity attachmentEntity) {
        return mongoTemplate.save(attachmentEntity);
    }
}
