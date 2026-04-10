package com.journalApp.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.journalApp.enums.Sentiment;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection ="journal_entries")
@Data
@NoArgsConstructor
public class JournalEntry {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Sentiment sentiment;

    private LocalDateTime date;
}
