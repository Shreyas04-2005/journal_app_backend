package com.journalApp.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.journalApp.enums.Sentiment;
import jakarta.validation.constraints.Email;
import jdk.jfr.BooleanFlag;
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

    @Indexed(unique = true)
    @Email
    private String email;

    private boolean sentimentAnalysis;

    @DBRef
    private List<JournalEntry> journalEntries=new ArrayList<>();//reference of journal_entries to journalEntries

    private List<String> roles;
}
