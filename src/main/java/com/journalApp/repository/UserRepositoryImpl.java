package com.journalApp.repository;


import com.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate; //mongo template is used as interface to interact with database

    public List<User> getUsersForSA(){ //this method is used for getting mood of user based on their journals
        Query query=new Query(); //query is used to save criteria of which we want
        query.addCriteria(Criteria.where("email").regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$")); //regex is regular expression for email
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));//if a user has its email and has its sentiments then this user get return
        return mongoTemplate.find(query,User.class);
    }

}
