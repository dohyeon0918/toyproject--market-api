package com.example.market_api.common.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){ super(message);}
}
