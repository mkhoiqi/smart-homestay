package com.rzq.smarthomestay.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class CustomException extends RuntimeException{
    private final HttpStatus status;
    private final String field;
    private final String message;
}
