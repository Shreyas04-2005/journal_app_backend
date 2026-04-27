package com.journalApp.service;

import com.journalApp.dto.CreateUserDto;
import com.journalApp.dto.GetUsersDto;
import com.journalApp.dto.ResetPasswordDto;
import com.journalApp.dto.UpdateUserDto;
import com.journalApp.entity.PasswordResetToken;
import com.journalApp.entity.User;
import com.journalApp.repository.PasswordRepository;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.*;

@Service  //we can use component annotation here but service is used for readability
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordRepository passwordRepository;

    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


    @Transactional
    public User registerUser(CreateUserDto dto) {

        if (userRepository.existsByUsername(dto.getUsername()) || userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Duplicate username or email");
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


    public List<GetUsersDto> getAll(){
        List<User>users=userRepository.findAll();
        return users.stream().map(user ->{
            GetUsersDto dto=new GetUsersDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setSentimentAnalysis(user.isSentimentAnalysis());
            dto.setUpdatedBy(user.getUpdatedBy());
            dto.setUpdatedAt(user.getUpdatedAt());
            dto.setRoles(user.getRoles());
            return dto;
        }).toList();

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

    public User updateUser(String id, UpdateUserDto body,String userName){
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
        existingUser.setUpdatedBy(userName);
        existingUser.setUpdatedAt(LocalDateTime.now());
       return userRepository.save(existingUser);
    }

    public ResponseEntity<?>forgotPassword(String email){
        Optional<User> existUser =userRepository.findByEmail(email);
        if(existUser.isEmpty()){
            return ResponseEntity.status(200).body("If the email exist, the reset link has been sent✅");
        }

        User user=existUser.get();

        //create random token
        String token= UUID.randomUUID().toString();

        //token expiry
        LocalDateTime expiry=LocalDateTime.now().plusMinutes(15);

        //save token
        PasswordResetToken passwordResetToken=new PasswordResetToken();
        passwordResetToken.setUserId(user.getId());
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(expiry);

        passwordRepository.save(passwordResetToken);

        //reset link with token
        String resetLink="https://journal-app-frontend.com/forgot-pasword?token=" + token;

        //send mail
        emailService.sendEmail(email,"Reset Password⚙","Click here to reset password: "+resetLink+" the link is expired in 15 minutes");

        return ResponseEntity.status(200).body("If the email exist, the reset link has been sent✅");
    }

    public ResponseEntity<?>resetPassword(ResetPasswordDto dto){
        //check new and confirm password
        if(!dto.getNewPassword().equals(dto.getConfirmNewPassword())){
            return ResponseEntity.status(400).body("Passwords do not match");
        }

        //get token
        Optional<PasswordResetToken>token=passwordRepository.findByToken(dto.getToken());
        if (token.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        //get token info
        PasswordResetToken resetToken=token.get();

        //check expiry
        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now())){
            return ResponseEntity.status(400).body("Token expired");
        }

        //find and update user
        ObjectId id=new ObjectId(resetToken.getUserId());
        User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with id "+id));
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        //delete token after update
        passwordRepository.delete(resetToken);

        return ResponseEntity.status(200).body("Password reset successful✅");
    }
}
