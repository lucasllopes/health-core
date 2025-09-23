package com.healthcore.appointmentservice.exception;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(Long doctorId) {
        super("Médico não encontrado com ID: " + doctorId);
    }
}