package com.journalApp.service;

import com.journalApp.dto.CreateJournalEntryDto;
import com.journalApp.entity.JournalEntry;
import com.journalApp.entity.User;
import com.journalApp.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userservice;


    @Transactional
    public JournalEntry saveEntry(CreateJournalEntryDto journalEntry, String username){
        try {
            User user=userservice.findByusername(username);
            JournalEntry entry=new JournalEntry();
            entry.setTitle(journalEntry.getTitle());
            entry.setContent(journalEntry.getContent());
            entry.setSentiment(journalEntry.getSentiment());
            entry.setDate(LocalDateTime.now());
            JournalEntry saved=journalEntryRepository.save(entry);
            user.getJournalEntries().add(saved);
            userservice.saveuser(user);
            return entry;
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("An error occurred while saving the entry.",e);
        }
    }

    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }

    public JournalEntry findbyid(ObjectId id){
        return journalEntryRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("journal not found with id "+id));
    }


    public JournalEntry deleteById(ObjectId id){
        JournalEntry entry=journalEntryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("journal not found with id "+id));
        journalEntryRepository.deleteById(id);
        return entry;
    }

}
