package com.example.datawarehouseserver.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum CodeException {

    CONFIG_NOT_FOUND(404, "Config not found", HttpStatus.NOT_FOUND),
    LOG_NOT_FOUND(404, "Log not found", HttpStatus.NOT_FOUND);

    int code;
    String message;
    HttpStatusCode httpStatusCode;

    public AppException throwException() {
        return new AppException(this);
    }
}
