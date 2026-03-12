package com.edigest.journalApp.controller;

import com.edigest.journalApp.entity.User;
import com.edigest.journalApp.service.UserDetailsServiceImpl;
import com.edigest.journalApp.service.UserService;
import com.edigest.journalApp.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/public")
@Slf4j
public class PublicController {

    @Autowired
    private UserService userService;

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

@PostMapping("/signup")
public ResponseEntity<String> signup(@RequestBody User user){
    String name=user.getUsername();
    String password=user.getPassword();
    if(name==null || password==null){
        return new ResponseEntity<>("username and password required",HttpStatus.BAD_REQUEST);
    }
    else if( userService.savenewUser(user)){
        System.out.println("Registration successfull");
        return new ResponseEntity<>("Registration successfull \n"+
               "UserId :" +user.getId()+"\n"+
                "UserName :"+user.getUsername()+"\n"+
                "journalEntries :"+user.getJournalEntries(),HttpStatus.CREATED);
    }
        return new ResponseEntity<>("Username already taken",HttpStatus.BAD_REQUEST);
}
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user){
        System.out.println(user);
    String name=user.getUsername();
    String password=user.getPassword();
    if(name==null || password==null){
        return  new ResponseEntity<>("username and password required",HttpStatus.BAD_REQUEST);
    }
    try {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwt=jwtUtil.generateToken(userDetails.getUsername());
        return new ResponseEntity<>("access token :"+jwt,HttpStatus.OK);
    } catch (Exception e){
        log.error("Exception occurred while createAuthenticationToken",e);
        return new ResponseEntity<>("Incorrect username or password",HttpStatus.BAD_REQUEST);
    }
}
}
