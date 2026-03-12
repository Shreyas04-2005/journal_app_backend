package com.journalApp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendMail(){
        emailService.sendEmail("spatil7451@gmail.com","Testing java mail Sender","hi how are u");
    }


}
