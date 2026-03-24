package com.journalApp.controller;

import com.journalApp.entity.User;
import com.journalApp.service.EmailService;
import com.journalApp.service.UserDetailsServiceImpl;
import com.journalApp.service.UserService;
import com.journalApp.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/public")
@Slf4j
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;


@GetMapping("/health-check")
public String healthcheck(){
    return "Welcome back";
}

@Transactional
@PostMapping("/signup")
public ResponseEntity<String> signup(@RequestBody User user){
    String name=user.getUsername();
    String password=user.getPassword();
    if(name==null || password==null){
        return new ResponseEntity<>("username and password required",HttpStatus.BAD_REQUEST);
    }
    else if( userService.savenewUser(user)){
        if(user.getEmail()!=null) {
            try {
                emailService.sendEmail(user.getEmail(), "Welcome to JournalApp " + user.getUsername() + " 🚀", "You have successfully registered to journalApp, now you can create journals for your daily schedule to improve yourself. \n Get the best of you✅");
                System.out.println("mail send to " + user.getEmail());
            }catch (Exception e){
                throw new RuntimeException("Error while sending mail"+e);
            }
        }
        System.out.println("Registration successfull");
        return new ResponseEntity<>("Registration successfull \n"+
               "UserId :" +user.getId()+"\n"+
                "UserName :"+user.getUsername()+"\n"+
                "journalEntries :"+user.getJournalEntries(),HttpStatus.CREATED);
    }
        return new ResponseEntity<>("Username already taken",HttpStatus.BAD_REQUEST);
}
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
    try {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwt=jwtUtil.generateToken(userDetails.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);

        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e){
        log.error("Exception occurred while createAuthenticationToken",e);
        return new ResponseEntity<>("Incorrect username or password",HttpStatus.BAD_REQUEST);
    }
}
}
