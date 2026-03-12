package com.journalApp.cache;

import com.journalApp.entity.ConfigJournalAppEntity;
import com.journalApp.repository.ConfigJournalAppRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Appcache {

    public enum Keys{
        WEATHER_API;
    }

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    public Map<String,String> appCache;

    @PostConstruct //when the Appcache bean is called the init method calls
    public void init(){
        appCache =new HashMap<>();
        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();
        for(ConfigJournalAppEntity configJournalAppEntity:all){
            appCache.put(configJournalAppEntity.getKey(),configJournalAppEntity.getValue());
        }
    }
}
