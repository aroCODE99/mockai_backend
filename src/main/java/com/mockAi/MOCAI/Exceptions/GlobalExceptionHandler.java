package com.mockAi.MOCAI.Exceptions;

import com.mockAi.MOCAI.Dtos.Response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException e) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("EMAIL_ALREADY_EXISTS", e.getMessage(), LocalDateTime.now().toString())
        );
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedUser(UnauthorizedUserException e)  {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ErrorResponse("UNAUTHORIZED", e.getMessage(), LocalDateTime.now().toString())
        );
    }

}
