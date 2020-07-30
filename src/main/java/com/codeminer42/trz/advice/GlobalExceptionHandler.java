package com.codeminer42.trz.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(ValidationError::new).collect(Collectors.toList());

        Map<String, Object> body = initBadRequestBody();
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String value = ex.getValue().toString();
        String name = ex.getName();
        String required = ex.getRequiredType().getSimpleName().toLowerCase();
        String message = String.format("\"%s\" is not a valid value. %s should be a %s", value, name, required);

        Map<String, Object> body = initBadRequestBody();
        body.put("message", message);
        return ResponseEntity.badRequest().body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        InvalidFormatException ex = (InvalidFormatException) e.getCause();

        String value = ex.getValue().toString();
        String name = ex.getPath().stream()
                .map(path -> {
                    String fieldName = path.getFieldName();
                    return fieldName == null ? "[]" : fieldName;
                })
                .reduce("", (accumulator, path) -> accumulator.concat(path.concat(".")));
        name = name.substring(0, name.length() - 1); // removes last dot
        name = name.replaceAll(".\\[]", "[]");

        String message;
        if (ex.getTargetType().isEnum()) {
            String required = Stream.of(ex.getTargetType().getEnumConstants())
                    .map(constant -> constant.toString())
                    .reduce("[",
                            (accumulator, constant) -> accumulator.concat(String.format("%s, ", constant)));
            required = required.substring(0, required.length() - 2).concat("]");
            message = String.format("\"%s\" is not a valid value. %s should be one of %s", value, name, required);
        } else {
            String required = ex.getTargetType().getSimpleName().toLowerCase();
            message = String.format("\"%s\" is not a valid value. %s should be a %s", value, name, required);
        }

        Map<String, Object> body = initBadRequestBody();
        body.put("message", message);
        return ResponseEntity.badRequest().body(body);
    }

    private Map<String, Object> initBadRequestBody() {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        return body;
    }
}

