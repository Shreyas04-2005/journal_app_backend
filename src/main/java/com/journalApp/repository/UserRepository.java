package com.journalApp.repository;

import com.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> { //Repository interface is used for communicate with database by defining services with mongorepository
    User findByUsername(String username);

    Boolean existsByUsername(String username);

    void deleteByUsername(String username);

    Boolean existsByEmail(String email);
}



