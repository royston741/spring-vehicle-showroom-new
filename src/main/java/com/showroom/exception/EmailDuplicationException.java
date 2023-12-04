package com.showroom.exception;

public class EmailDuplicationException extends RuntimeException{
    public EmailDuplicationException(String statement){
        super(statement);
    }
}
