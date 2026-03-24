package com.journalApp.controller;

import com.journalApp.cache.Appcache;
import com.journalApp.entity.User;
import com.journalApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        List<User> all = userService.getAll();
        if (all != null && !all.isEmpty()) {
            Map<String,Object> response = new HashMap<>();
            response.put("totalUsers", all.size());
            response.put("users", all);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin-user")
    public void creteuser(@RequestBody User user){
        userService.saveAdmin(user);
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
            return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/updateById/{id}")
    public  ResponseEntity<?>updateById(@PathVariable("id")String id,@RequestBody User body){
        try {
            User updatedUser = userService.updateUser(id, body);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }catch(RuntimeException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/deleteById")
    public ResponseEntity<?>deleteUserById(@PathVariable("id")String id){
        if(id==null || id.isEmpty()){
            return  new ResponseEntity<>("id required in path",HttpStatus.BAD_REQUEST);
        }
        if(!ObjectId.isValid(id)){
            return new ResponseEntity<>("Invalid ObjectId format", HttpStatus.BAD_REQUEST);
        }
        ObjectId objectId = new ObjectId(id);
        User user=userService.getUserById(objectId);
        if(user==null){
            log.error("User not found");
            return  new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        }
         userService.deleteById(objectId);
        Map<String,Object> response = new HashMap<>();
        response.put("message","User deleted");
        response.put("user",user);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

}
