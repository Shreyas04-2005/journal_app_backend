package com.journalApp.controller;

import com.journalApp.cache.Appcache;
import com.journalApp.dto.CreateUserDto;
import com.journalApp.dto.GetUsersDto;
import com.journalApp.dto.UpdateUserDto;
import com.journalApp.entity.User;
import com.journalApp.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private Appcache appcache;

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllusers() {
        List<GetUsersDto> all = userService.getAll();
        if (all != null && !all.isEmpty()) {
            Map<String,Object> response = new HashMap<>();
            response.put("totalUsers", all.size());
            response.put("users", all);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<?> creteuser(@Valid @RequestBody CreateUserDto user){
       User admin= userService.saveAdmin(user);

       if(admin==null){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicate username or email");
       }
        Map<String,String>userMap=new LinkedHashMap<>();
        userMap.put("userId",admin.getId());
        userMap.put("username",admin.getUsername());
        userMap.put("email",admin.getEmail());
        return  ResponseEntity.status(HttpStatus.CREATED).body(userMap);
    }

    @GetMapping("/clear-cache")
    public void clearAppCache(){
        appcache.init();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid id or Invalid id format",HttpStatus.BAD_REQUEST);
        }
        User user=userService.getUserById(objectId);
        Map<String,Object>map=new LinkedHashMap<>();
        map.put("id",user.getId());
        map.put("username",user.getUsername());
        map.put("email",user.getEmail());
        map.put("sentimentAnalysis",user.isSentimentAnalysis());
        map.put("updatedBy",user.getUpdatedBy());
        map.put("updatedAt",user.getUpdatedAt());
            return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PatchMapping("/updateById/{id}")
    public  ResponseEntity<?>updateById(@PathVariable("id")String id,@Valid @RequestBody UpdateUserDto body){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        userService.updateUser(id, body,username);
            return new ResponseEntity<>("User Updated", HttpStatus.OK);
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?>deleteUserById(@PathVariable("id")String id){
        if(id==null || id.isEmpty()){
            return  new ResponseEntity<>("id required in path",HttpStatus.BAD_REQUEST);
        }
        if(!ObjectId.isValid(id)){
            return new ResponseEntity<>("Invalid ObjectId format", HttpStatus.BAD_REQUEST);
        }
        ObjectId objectId = new ObjectId(id);
         userService.deleteById(objectId);

        return new ResponseEntity<>("User deleted",HttpStatus.OK);

    }

}
