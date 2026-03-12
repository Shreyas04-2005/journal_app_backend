package com.journalApp.controller;

import com.journalApp.cache.Appcache;
import com.journalApp.entity.User;
import com.journalApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/getById")
    public ResponseEntity<?> getUserById(@RequestParam("id") String id){
        if(id==null || id.isEmpty()){
            return  new ResponseEntity<>("id required in params",HttpStatus.BAD_REQUEST);
        }
        if(!ObjectId.isValid(id)){
            return new ResponseEntity<>("Invalid ObjectId format", HttpStatus.BAD_REQUEST);
        }
        ObjectId objectId = new ObjectId(id);
        Optional<User> user= userService.findbyid(objectId);
        if(user.isEmpty()){
            return new ResponseEntity<>("invalid id",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.get(),HttpStatus.OK);
    }

    @DeleteMapping("/deleteById")
    public ResponseEntity<?>deleteUserById(@RequestParam("id")String id){
        if(id==null || id.isEmpty()){
            return  new ResponseEntity<>("id required in params",HttpStatus.BAD_REQUEST);
        }
        if(!ObjectId.isValid(id)){
            return new ResponseEntity<>("Invalid ObjectId format", HttpStatus.BAD_REQUEST);
        }
        ObjectId objectId = new ObjectId(id);
        Optional<User>user =userService.findbyid(objectId);
        if(user.isEmpty()){
            log.error("User not found");
            return  new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        }
         userService.deleteById(objectId);
        Map<String,Object> response = new HashMap<>();
        response.put("message","User deleted");
        response.put("user",user.get());
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

}
