package com.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

@Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to,String subject,String body){ //send email to provided email and u have to add properties of mail sender in properties.yml file
        try{
            SimpleMailMessage mail=new SimpleMailMessage(); //new object
            mail.setTo(to); //the person which u have to send mail
            mail.setSubject(subject); //subject of mail
            mail.setText(body); //body of mail
            javaMailSender.send(mail); //the javamailsender is a mailsender by java by adding its dependencies
        }catch(Exception e){
            log.error("Exception while sendEmail",e);
        }
    }

}
