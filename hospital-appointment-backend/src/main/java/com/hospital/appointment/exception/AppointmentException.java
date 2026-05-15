package com.hospital.appointment.exception;

/**
 * 挂号业务异常
 */
public class AppointmentException extends RuntimeException {

    public AppointmentException(String message) {
        super(message);
    }
}