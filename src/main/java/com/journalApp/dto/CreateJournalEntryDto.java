package com.journalApp.dto;

import com.journalApp.enums.Sentiment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateJournalEntryDto {

    @NotBlank(message = "title is required")
    @Size(min = 2, message = "title must be at least 2 characters")
    private String title;

    @NotBlank(message = "content is required")
    @Size(min = 4, message = "content must be at least 4 characters")
    private String content;

    private Sentiment sentiment;
}
