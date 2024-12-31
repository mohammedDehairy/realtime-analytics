package com.eldoheiri.realtime_analytics.exceptionhandling;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.eldoheiri.realtime_analytics.dataobjects.error.ErrorResponse;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.DeviceException;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.heartbeat.HeartBeatException;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.session.SessionException;

@ControllerAdvice
public class ServerExceptionController {
    @ExceptionHandler(value = SessionException.class)
    public ResponseEntity<ErrorResponse> exception(SessionException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(500);
        errorResponse.setMessage("Failed to record Session");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(value = HeartBeatException.class)
    public ResponseEntity<ErrorResponse> exception(HeartBeatException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(500);
        errorResponse.setMessage("Failed to record heart beat");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(value = DeviceException.class)
    public ResponseEntity<ErrorResponse> exception(DeviceException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(500);
        errorResponse.setMessage("Failed to create new device.");
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
