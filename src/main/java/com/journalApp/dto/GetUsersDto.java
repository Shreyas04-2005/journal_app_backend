package com.journalApp.dto;

import com.journalApp.enums.Sentiment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GetUsersDto {
        private String id;
        private String username;
        private String email;
        private boolean sentimentAnalysis;
        private String updatedBy;
        private LocalDateTime updatedAt;
        private List<String>roles;
    }

