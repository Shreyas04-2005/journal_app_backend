package com.journalApp.exception;

import jakarta.mail.Header;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;


@RestControllerAdvice
public class globalException {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String>handleResourceNotFoundException(ResourceNotFoundException ex){

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body="{\"message\": \""+ex.getMessage()+"\"}";
        return new ResponseEntity<>(body,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String>handleInputValidationException(MethodArgumentNotValidException ex){
        String message=ex.getBindingResult().
                getFieldErrors().
                stream().
                map(error ->  error.getDefaultMessage()).
                findFirst().
                orElse("Invalid Input");

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body="{\"message\": \""+message+"\"}";

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String>handleEnumException(HttpMessageNotReadableException ex){

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body="{\"message\": \""+ex.getMessage()+"\"}";

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String>handleConflictException(DuplicateResourceException ex){

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body="{\"message\": \""+ex.getMessage()+"\"}";

        return new ResponseEntity<>(body,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNotFound() {
        return ResponseEntity.status(404).body("Endpoint not found ❌");
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String>wetherResponceNotFoundException(HttpClientErrorException ex){

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body="{\"message\":\"Weather not found for the given city\"}";

        return new ResponseEntity<>(body,headers,HttpStatus.NOT_FOUND);
    }

}
