package com.alok91340.gethired.exception;


import org.springframework.http.HttpStatus;

public class GetHiredException extends RuntimeException {

    private String message;
    private HttpStatus status;

    public GetHiredException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public GetHiredException(String message, String message1, HttpStatus status) {
        super(message);
        this.message = message1;
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
