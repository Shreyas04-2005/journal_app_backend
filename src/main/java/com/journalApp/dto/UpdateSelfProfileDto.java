package com.journalApp.dto;

import jakarta.validation.constraints.Email;
import jdk.jfr.BooleanFlag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSelfProfileDto {

    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @BooleanFlag
    private Boolean sentimentAnalysis;
}
