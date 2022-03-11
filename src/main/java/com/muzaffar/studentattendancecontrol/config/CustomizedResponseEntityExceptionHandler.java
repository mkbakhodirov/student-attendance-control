package com.muzaffar.studentattendancecontrol.config;

import com.muzaffar.studentattendancecontrol.exception.MissRequiredParam;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.exception.NotValidParamException;
import com.muzaffar.studentattendancecontrol.exception.UniqueException;
import com.muzaffar.studentattendancecontrol.model.response.ApiExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UniqueException.class)
    public final ResponseEntity<Object> handleUniqueException(UniqueException exception, WebRequest webRequest) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse();
        apiExceptionResponse.setMessage(exception.getMessage());
        apiExceptionResponse.setDetails(webRequest.getDescription(false));
        return new ResponseEntity<>(apiExceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MissRequiredParam.class)
    public final ResponseEntity<Object> handleMissRequiredParamException(MissRequiredParam exception, WebRequest webRequest) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse();
        apiExceptionResponse.setMessage(exception.getMessage());
        apiExceptionResponse.setDetails(webRequest.getDescription(false));
        return new ResponseEntity<>(apiExceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException exception, WebRequest webRequest) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse();
        apiExceptionResponse.setMessage(exception.getMessage());
        apiExceptionResponse.setDetails(webRequest.getDescription(false));
        return new ResponseEntity<>(apiExceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotValidParamException.class)
    public final ResponseEntity<Object> handleNotValidParamException(NotValidParamException exception, WebRequest webRequest) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse();
        apiExceptionResponse.setMessage(exception.getMessage());
        apiExceptionResponse.setDetails(webRequest.getDescription(false));
        return new ResponseEntity<>(apiExceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest webRequest) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse();
        apiExceptionResponse.setMessage(exception.getMessage());
        apiExceptionResponse.setDetails(webRequest.getDescription(false));
        return new ResponseEntity<>(apiExceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
