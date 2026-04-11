package com.journalApp.controller;

import com.journalApp.API.Responce.WeatherResponce;
import com.journalApp.entity.User;
import com.journalApp.repository.UserRepository;
import com.journalApp.service.UserService;
import com.journalApp.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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
        WeatherResponce weatherResponce = weatherService.getweather(city);
        String greting = "";

            greting = ", Weather feels like " + weatherResponce.getCurrent().getFeelsLike() + "\n" +
                    "Temperature is " + weatherResponce.getCurrent().getTemperature() + "°C" + "\n" +
                    "Environment is like " + weatherResponce.getCurrent().getWeatherDescriptions() + "\n" +
                    "In " + city;
            return new ResponseEntity<>("Hi " + authentication.getName() + greting, HttpStatus.OK);

    }
}
