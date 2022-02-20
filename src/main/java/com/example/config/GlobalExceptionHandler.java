package com.example.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public @ResponseBody
    CallError handleInternalServerError(Exception exception) {
        return handleException(exception);
    }

    private CallError handleException(Exception exception) {
        log.error(String.format("Catch exception %s", exception.getMessage()), exception);
        return new CallError(exception.getMessage(), exception.getClass().getSimpleName());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallError {
        private String message;
        private String className;
    }
}
