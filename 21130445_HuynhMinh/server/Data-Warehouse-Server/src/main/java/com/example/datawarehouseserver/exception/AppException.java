package com.example.datawarehouseserver.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppException extends RuntimeException {

    CodeException codeException;
}
