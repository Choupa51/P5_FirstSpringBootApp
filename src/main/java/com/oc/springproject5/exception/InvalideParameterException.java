package com.oc.springproject5.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public class InvalideParameterException extends RuntimeException {
        public InvalideParameterException(String message) {
            super(message);
        }
    }
