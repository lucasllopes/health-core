package com.healthcore.appointmentservice.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long patientId) {
        super("Paciente não encontrado com ID: " + patientId);
    }
}