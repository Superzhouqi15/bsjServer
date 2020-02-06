package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Classifier;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author dav1d
 */
@Component
public class ClassifierTemplate {
    @Autowired
    MongoTemplate mongoTemplate;

    public void save(Classifier classifier) {
        mongoTemplate.save(classifier);
    }

    public void addCompetition(List<String> type, ObjectId objectId) {
        Iterator<String> it = type.iterator();
        String tempType;
        Query query;
        Classifier classifier;
        while (it.hasNext()) {
            tempType = it.next();
            query = Query.query(Criteria.where("type").is(tempType));
            classifier = mongoTemplate.findOne(query, Classifier.class);
            if (classifier == null) {
                classifier = new Classifier();
                classifier.setType(tempType);
            }
            classifier.addCompetition(objectId);
            mongoTemplate.save(classifier);
        }
    }

    public List<ObjectId> findByType(String type) {
        Query query = Query.query(Criteria.where("type").is(type));
        return Objects.requireNonNull(mongoTemplate.findOne(query, Classifier.class)).getCompetitionList();
    }

}