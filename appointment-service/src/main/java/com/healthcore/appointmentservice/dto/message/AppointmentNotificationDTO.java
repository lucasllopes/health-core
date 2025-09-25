package com.healthcore.appointmentservice.dto.message;

import java.time.LocalDateTime;

public record AppointmentNotificationDTO(
        Long id,
        Long patientId,
        Long doctorId,
        Long nurseId,
        LocalDateTime appointmentDate,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
