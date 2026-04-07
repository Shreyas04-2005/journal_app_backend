package com.journalApp.service;

import com.journalApp.dto.CreateUserDto;
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
import org.springframework.transaction.annotation.Transactional;

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
        return userRepository.findByUsername(username);
    }

    @Transactional
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
            emailService.sendEmail(existingUser.getEmail(),"Changing Email 🔗","Hi "+existingUser.getUsername()+" we are informing that you have changed your email,\n From now all the new emails are sent to your new email "+body.getEmail()+".\n"+"If its not u then click on the link below , thank you.");
            emailService.sendEmail(body.getEmail(), "Welcome to JournalApp " + body.getUsername() + " 🚀", "You have successfully registered to journalApp, now you can create journals for your daily schedule to improve yourself. \n Get the best of you✅");
            existingUser.setEmail(body.getEmail());
        }
       return userRepository.save(existingUser);
    }
}
