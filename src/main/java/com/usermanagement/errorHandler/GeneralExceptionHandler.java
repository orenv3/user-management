package com.usermanagement.errorHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GeneralExceptionHandler.class);

    @ExceptionHandler({ ProtectedUserException.class })
    protected ResponseEntity<ApiErrorResponse> handleProtectedUserException(ProtectedUserException e, HttpServletRequest request) {
        log.warn("Protected user operation rejected. message={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiErrorResponse.of(
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    protected ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        e.getConstraintViolations().forEach(v -> {
            String path = v.getPropertyPath().toString();
            String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
            fieldErrors.putIfAbsent(field, v.getMessage());
        });
        String message = summarizeFieldErrors(fieldErrors);
        log.warn("Constraint violation. message={}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        fieldErrors
                )
        );
    }

    @ExceptionHandler({ UserValidationErrorException.class })
    protected ResponseEntity<ApiErrorResponse> handleValidationErrorException(UserValidationErrorException e, HttpServletRequest request) {
        log.warn("User validation error. message={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiErrorResponse.of(
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        e.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler({ TaskGeneralErrorException.class })
    protected ResponseEntity<ApiErrorResponse> handleTaskGeneralErrorException(TaskGeneralErrorException e, HttpServletRequest request) {
        log.warn("Task error. message={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler({ CommentGeneralErrorException.class })
    protected ResponseEntity<ApiErrorResponse> handleCommentGeneralErrorException(CommentGeneralErrorException e, HttpServletRequest request) {
        log.warn("Comment error. message={}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage(),
                        request.getRequestURI()
                )
        );
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.putIfAbsent(err.getField(), err.getDefaultMessage()));

        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                summarizeFieldErrors(fieldErrors),
                request.getDescription(false).replace("uri=", ""),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private static String summarizeFieldErrors(Map<String, String> fieldErrors) {
        if (fieldErrors == null || fieldErrors.isEmpty()) {
            return "Validation failed";
        }
        return fieldErrors.values().stream().collect(Collectors.joining("; "));
    }
}
