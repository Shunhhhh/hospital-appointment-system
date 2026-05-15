package com.hospital.appointment.exception;

import com.hospital.appointment.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 挂号业务异常处理
 */
@RestControllerAdvice
public class AppointmentExceptionAdvice {

    @ExceptionHandler(AppointmentException.class)
    public Result<Void> handleAppointmentException(AppointmentException exception) {
        return Result.error(exception.getMessage());
    }
}