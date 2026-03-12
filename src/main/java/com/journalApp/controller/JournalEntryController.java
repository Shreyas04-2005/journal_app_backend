package com.journalApp.controller;

import com.journalApp.entity.JournalEntry;
import com.journalApp.entity.User;
import com.journalApp.service.JournalEntryService;
import com.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userservice;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user = userservice.findByusername(username);
        List<JournalEntry> all = user.getJournalEntries();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("id/{myid}")
    public ResponseEntity<JournalEntry> getjournalEntryById(@PathVariable ObjectId myid) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user = userservice.findByusername(username);
       List<JournalEntry>collect= user.getJournalEntries().stream().filter(x->x.getId().equals(myid)).collect(Collectors.toList());
       if(!collect.isEmpty()){
           Optional<JournalEntry> journalEntry = journalEntryService.findbyid(myid);
           if (journalEntry.isPresent()) {
               return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
           }
       }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myentry) { // ? refers to any non defined instance like JournalEntry in GetMapping by id
        try {
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            String username=authentication.getName();
            journalEntryService.saveEntry(myentry, username);
            return new ResponseEntity<>(myentry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("id/{myid}")
    public ResponseEntity<?> deletejournalEntrybyId(@PathVariable ObjectId myid) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        boolean removed=journalEntryService.deleteById(myid, username);
        if(removed){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
}

    @PutMapping("id/{myid}")
    public ResponseEntity<?> updateJournalById(@PathVariable ObjectId myid, @RequestBody JournalEntry newEntry){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user = userservice.findByusername(username);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x->x.getId().equals(myid)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            Optional<JournalEntry> journalEntry = journalEntryService.findbyid(myid);
            if (journalEntry.isPresent()) {
                JournalEntry old=journalEntry.get();
                old.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")? newEntry.getTitle() : old.getTitle());
                old.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("")? newEntry.getContent() : old.getContent());
                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
