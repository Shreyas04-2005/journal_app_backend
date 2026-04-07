package com.journalApp.exception;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class globalException {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String>handleResourceNotFoundException(ResourceNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String>handleInputValidationException(MethodArgumentNotValidException ex){
        String message=ex.getBindingResult().
                getFieldErrors().
                stream().
                map(error ->  error.getDefaultMessage()).
                findFirst().
                orElse("Invalid Input");

        return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
    }
}
