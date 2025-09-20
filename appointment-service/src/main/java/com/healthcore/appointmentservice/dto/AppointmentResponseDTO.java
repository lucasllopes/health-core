package com.healthcore.appointmentservice.dto;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
    Long id,
    Long patientId,
    Long doctorId,
    Long nurseId,
    LocalDateTime appointmentDate,
    String status,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long medicalRecordId
) {}
