package com.alok91340.gethired.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Resource not found handler
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GetHiredException> handleResourceNotFoundException(ResourceNotFoundException exception,
            WebRequest webRequest) {
        GetHiredException errorDetail = new GetHiredException(exception.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDetail, HttpStatus.NOT_FOUND);
    }

    // Global Exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GetHiredException> handleGlobalException(Exception exception, WebRequest webRequest) {
        GetHiredException errorDetail = new GetHiredException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
