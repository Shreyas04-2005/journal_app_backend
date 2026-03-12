package com.journalApp.controller;

import com.journalApp.API.Responce.WeatherResponce;
import com.journalApp.entity.User;
import com.journalApp.repository.UserRepository;
import com.journalApp.service.UserService;
import com.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class Usercontroller {

    @Autowired
    private UserService userservice;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    WeatherService weatherService;


    @PutMapping
    public ResponseEntity<?> updateuser(@RequestBody User user){
       Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User userIndb=userservice.findByusername(username);
            userIndb.setUsername(user.getUsername());
            userIndb.setPassword(user.getPassword());
            userservice.savenewUser(userIndb);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @DeleteMapping
    public ResponseEntity<?> deletebyid(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUsername(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<?> gretings(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        WeatherResponce weatherResponce = weatherService.getweather("Kolhapur");
        String greting="";
        if(weatherResponce!=null){
            greting=", Weather feels like "+ weatherResponce.getCurrent().getFeelsLike();
        }
        return new ResponseEntity<>("Hi "+authentication.getName() + greting, HttpStatus.OK);

    }

}
