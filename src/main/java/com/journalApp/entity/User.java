package com.journalApp.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection ="users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    @NonNull
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NonNull
    private String password;

    private String email;

    private Boolean sentimentAnalysis;


    @DBRef
    private List<JournalEntry> journalEntries=new ArrayList<>();//reference of journal_entries to journalEntries

    private List<String> roles;
}
