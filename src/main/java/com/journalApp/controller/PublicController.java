package com.journalApp.controller;

import com.journalApp.dto.CreateUserDto;
import com.journalApp.dto.LoginUserDto;
import com.journalApp.dto.ResetPasswordDto;
import com.journalApp.entity.User;
import com.journalApp.service.EmailService;
import com.journalApp.service.UserDetailsServiceImpl;
import com.journalApp.service.UserService;
import com.journalApp.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public ResponseEntity<?> healthcheck(){
    Map<String,Object>map=new HashMap<>();
    map.put("Status","UP");
    map.put("application", "Journal App Backend");
    map.put("timestamp", LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.OK).body(map);
}


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody CreateUserDto userDTO) {

        User user = userService.registerUser(userDTO);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            emailService.sendEmailAsync(
                    user.getEmail(),
                    "Welcome to JournalApp 🚀",
                    "You have successfully registered to JournalApp."
            );
        }

        Map<String,String>userMap=new LinkedHashMap<>();
        userMap.put("userId",user.getId());
        userMap.put("username",user.getUsername());
        userMap.put("email",user.getEmail());
        return  ResponseEntity.status(HttpStatus.CREATED).body(userMap);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDto user){
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
    @PostMapping("/forgot-password")
    public ResponseEntity<?>forgotPassword(@RequestParam("email")String email){
        return userService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?>resetPassword(@Valid @RequestBody ResetPasswordDto dto){
        return userService.resetPassword(dto);
    }
}
