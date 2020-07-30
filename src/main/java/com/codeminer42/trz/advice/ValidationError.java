package com.codeminer42.trz.advice;

import lombok.Data;
import org.springframework.validation.FieldError;

@Data
public class ValidationError {

    private final String field;
    private final String message;

    public ValidationError(FieldError fieldError) {
        this.field = camelCaseToSnakeCase(fieldError.getField());
        this.message = fieldError.getDefaultMessage();
    }

    private String camelCaseToSnakeCase(String camelCasedString) {
        return camelCasedString.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}

