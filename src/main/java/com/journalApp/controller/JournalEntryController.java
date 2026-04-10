package com.journalApp.controller;

import com.journalApp.dto.CreateJournalEntryDto;
import com.journalApp.dto.UpdateJournalEntryDto;
import com.journalApp.entity.JournalEntry;
import com.journalApp.entity.User;
import com.journalApp.service.JournalEntryService;
import com.journalApp.service.UserService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journalEntry")
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
        Map<String,Object>map=new LinkedHashMap<>();
        if (all != null && !all.isEmpty()) {
            map.put("totalJournals",all.size());
            map.put("journals",all);
            return new ResponseEntity<>(map, HttpStatus.OK);
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
           JournalEntry journalEntry = journalEntryService.findbyid(myid);
           if (journalEntry!=null) {
               return new ResponseEntity<>(journalEntry, HttpStatus.OK);
           }
       }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEntry(@Valid @RequestBody CreateJournalEntryDto myentry) { // ? refers to any non defined instance like JournalEntry in GetMapping by id
        try {
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            String username=authentication.getName();
            JournalEntry entry=journalEntryService.saveEntry(myentry, username);
            Map<String,Object>journalMap=new LinkedHashMap<>();
            journalMap.put("journalId",entry.getId().toHexString());
            journalMap.put("title",entry.getTitle());
            journalMap.put("content",entry.getContent());
            journalMap.put("sentiment",entry.getSentiment());
            return ResponseEntity.status(HttpStatus.CREATED).body(journalMap);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("id/{myid}")
    public ResponseEntity<?> deletejournalEntrybyId(@PathVariable String myid) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        ObjectId id;
        try {
            id = new ObjectId(myid);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("Invalid id format",HttpStatus.BAD_REQUEST);
        }
        journalEntryService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("journal deleted");
}

    @PatchMapping("id/{myid}")
    public ResponseEntity<?> updateJournalById(@PathVariable ObjectId myid,@Valid @RequestBody UpdateJournalEntryDto newEntry){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user = userservice.findByusername(username);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x->x.getId().equals(myid)).collect(Collectors.toList());
            JournalEntry journalEntry = journalEntryService.findbyid(myid);
                journalEntry.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")? newEntry.getTitle() : journalEntry.getTitle());
                journalEntry.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("")? newEntry.getContent() : journalEntry.getContent());
                journalEntry.setSentiment(newEntry.getSentiment()!=null ? newEntry.getSentiment():journalEntry.getSentiment());
                journalEntryService.saveEntry(journalEntry);
                return new ResponseEntity<>(journalEntry,HttpStatus.OK);
    }

}
