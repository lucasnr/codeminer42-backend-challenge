package com.codeminer42.trz.advice;

import lombok.Data;
import org.springframework.validation.FieldError;

@Data
public class ValidationError {

    private final String field;
    private final String message;

    public ValidationError(FieldError fieldError) {
        this.field = fieldError.getField();
        this.message = fieldError.getDefaultMessage();
    }
}

