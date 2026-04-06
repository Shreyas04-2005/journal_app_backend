package com.journalApp.service;

import com.journalApp.entity.User;
import com.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service  //we can use component annotation here but service is used for readability
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


    public boolean savenewUser(User user){
        try {
            User exisingUser=userRepository.findByusername(user.getUsername());
            if(exisingUser!=null){
                log.error("username already taken");
                return false;
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.error("Exception occurred while creating user"+e);
           return false;
        }
    }

    public void saveuser(User user){
        userRepository.save(user);
    }

    public ResponseEntity<String> saveAdmin(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER","ADMIN"));
        userRepository.save(user);
        return new ResponseEntity<>("Admin created \n"+
                "userId :"+user.getId()+"\n"+
                "UserName :"+user.getUsername()+"\n"
                , HttpStatus.CREATED);
    }


    public List<User> getAll(){
        return userRepository.findAll();
    }

    public User getUserById(ObjectId id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id "+id));
    }

    public void deleteById(ObjectId id){
         userRepository.deleteById(id);
    }

    public User findByusername(String username){
        return userRepository.findByusername(username);
    }

    public User updateUser(String id, User body) {
        ObjectId objectId=new ObjectId(id);
        User existingUser=userRepository.findById(objectId).orElseThrow(()->new ResourceNotFoundException("User not found"));

        if(body.getUsername()!=null){
            existingUser.setUsername(body.getUsername());
        }
        if(body.getPassword()!=null){
            existingUser.setPassword(body.getPassword());
        }
        if(body.getEmail()!=null){
            existingUser.setEmail(body.getEmail());
        }
       return userRepository.save(existingUser);
    }
};
