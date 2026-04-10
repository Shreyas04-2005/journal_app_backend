package com.journalApp.dto;

import com.journalApp.enums.Sentiment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateJournalEntryDto {

    @Size(min = 2, message = "title must be at least 2 characters")
    private String title;

    @Size(min = 4, message = "content must be at least 4 characters")
    private String content;

    private Sentiment sentiment;
}
