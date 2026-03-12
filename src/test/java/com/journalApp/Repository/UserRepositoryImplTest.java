package com.journalApp.Repository;

import com.journalApp.repository.UserRepositoryImpl;
import com.mongodb.assertions.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest //it is used to test
public class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Test
    public void testfindbyusername(){
        Assertions.assertNotNull(userRepository.getUsersForSA());
    }
}
