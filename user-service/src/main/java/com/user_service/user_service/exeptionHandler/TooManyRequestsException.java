package com.user_service.user_service.exeptionHandler;

public class TooManyRequestsException extends RuntimeException{
    public TooManyRequestsException(String message){
        super(message);
    }
}
