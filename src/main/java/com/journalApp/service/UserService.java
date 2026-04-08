package com.journalApp.service;

import com.journalApp.dto.CreateUserDto;
import com.journalApp.dto.UpdateUserDto;
import com.journalApp.entity.User;
import com.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;

@Service  //we can use component annotation here but service is used for readability
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


    @Transactional
    public User registerUser(CreateUserDto dto) {

        if (userRepository.existsByUsername(dto.getUsername()) || userRepository.existsByEmail(dto.getEmail())) {
            return null;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRoles(List.of("USER"));
        boolean sentimentValue=Boolean.TRUE.equals(dto.getSentimentAnalysis());
        user.setSentimentAnalysis(sentimentValue);

        userRepository.save(user);

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            try {
                emailService.sendEmail(
                        dto.getEmail(),
                        "Welcome to JournalApp 🚀",
                        "You have successfully registered to JournalApp."
                );
            } catch (Exception e) {
                log.error("Email sending failed", e);
            }
        }

        return user;
    }

    public void saveuser(User user){
        userRepository.save(user);
    }

    public User saveAdmin(CreateUserDto user) {
        try {
            if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
                return null;
            }
            User inputUser = new User();
            inputUser.setUsername(user.getUsername());
            inputUser.setPassword(passwordEncoder.encode(user.getPassword()));
            inputUser.setEmail(user.getEmail());
            inputUser.setRoles(Arrays.asList("USER", "ADMIN"));
            inputUser.setSentimentAnalysis(false);
            userRepository.save(inputUser);
            return inputUser;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<User> getAll(){
        return userRepository.findAll();
    }

    public User getUserById(ObjectId id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id "+id));
    }

    public void deleteById(ObjectId id){
         User user= userRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("User not found with id "+id));
         if(user!=null){
             userRepository.deleteById(id);
         }
    }

    public User findByusername(String username){
        return userRepository.findByUsername(username);
    }

    public User updateUser(String id, UpdateUserDto body){
        ObjectId objectId=new ObjectId(id);
        User existingUser=userRepository.findById(objectId).orElseThrow(()->new ResourceNotFoundException("User not found"));

        if(body.getUsername()!=null){
           User existed= userRepository.findByUsername(body.getUsername());
           if(existed==null) existingUser.setUsername(body.getUsername());
           else throw new DuplicateResourceException("username already exists");
        }

        if(body.getEmail()!=null) {
            if (body.getEmail().equals(existingUser.getEmail()) || userRepository.existsByEmail(body.getEmail())) {
                throw new DuplicateResourceException("email already exists");
            }
            if (!body.getEmail().equals(existingUser.getEmail())) {
                emailService.sendEmail(existingUser.getEmail(), "Changing Email 🔗", "Hi " + existingUser.getUsername() + " we are informing that you have changed your email,\n From now all the new emails are sent to your new email " + body.getEmail() + ".\n" + "If its not u then click on the link below , thank you.");
                emailService.sendEmail(body.getEmail(), "Welcome to JournalApp " + body.getUsername() + " 🚀", "You have successfully registered to journalApp, now you can create journals for your daily schedule to improve yourself. \n Get the best of you✅");
                existingUser.setEmail(body.getEmail());
            }
        }

        if(body.getPassword()!=null){
            existingUser.setPassword(passwordEncoder.encode(body.getPassword()));
        }
        if(body.getSentimentAnalysis()!=null){
            existingUser.setSentimentAnalysis(body.getSentimentAnalysis());
        }
       return userRepository.save(existingUser);
    }
}
