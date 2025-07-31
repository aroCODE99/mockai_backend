package com.mockAi.MOCAI.Exceptions;

public class UnauthorizedUserException extends RuntimeException {

    public UnauthorizedUserException(String mes) {
        super(mes);
    }
}
