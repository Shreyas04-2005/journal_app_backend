package com.journalApp.controller;

import com.journalApp.API.Responce.WeatherResponce;
import com.journalApp.dto.UpdateSelfProfileDto;
import com.journalApp.repository.UserRepository;
import com.journalApp.service.UserService;
import com.journalApp.service.WeatherService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class Usercontroller {

    @Autowired
    private UserService userservice;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    WeatherService weatherService;


    @GetMapping
    public ResponseEntity<?> gretings(@RequestParam("city") String city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponce weatherResponse = weatherService.getweather(city);

        Map<String, Object>response=new LinkedHashMap<>();
        response.put("user", authentication.getName());
        response.put("city", city);
        response.put("feelsLike", weatherResponse.getCurrent().getFeelsLike());
        response.put("temperature", weatherResponse.getCurrent().getTemperature() + "°C");
        response.put("description", weatherResponse.getCurrent().getWeatherDescriptions());
        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping("/updateProfile")
    public ResponseEntity<?>updateProfile(@Valid @RequestBody UpdateSelfProfileDto body){
    Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
    String username=authentication.getName();
    userservice.updateProfile(username,body);
    return ResponseEntity.ok("Profile updated");
    }
}
