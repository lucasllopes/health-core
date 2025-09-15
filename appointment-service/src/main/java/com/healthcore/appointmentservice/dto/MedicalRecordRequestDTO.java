package com.healthcore.appointmentservice.dto;

import java.time.LocalDateTime;
public record MedicalRecordRequestDTO(
        Long id,
        Long appointmentId,
        Long doctorId,
        Long patientId,
        String diagnosis,
        String prescription,
        String observations,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}