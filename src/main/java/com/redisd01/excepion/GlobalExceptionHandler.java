package com.redisd01.excepion;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    private LocalDateTime timestamp;
//    private int status;
//    private String error;
//    private String message;

	@ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleEmployeeNotFound(EmployeeNotFoundException ex) {
        ErrorMessage error = new ErrorMessage(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(EmployeeAlreadyExistException.class)
	public ResponseEntity<ErrorMessage> handleEmployeeAlreadyExists(EmployeeAlreadyExistException ex) {
	    ErrorMessage error = new ErrorMessage(
	            LocalDateTime.now(),
	            HttpStatus.CONFLICT.value(),
	            "Conflict",
	            ex.getMessage()
	    );
	    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericException(Exception ex) {
        ErrorMessage error = new ErrorMessage(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericRuntimeException(RuntimeException ex) {
        ErrorMessage error = new ErrorMessage(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
