package com.healthcore.notificationservice.dto;

import java.time.LocalDateTime;

public record NotificationMessageDTO(
        Long id,
        Long patientId,
        Long doctorId,
        Long nurseId,
        LocalDateTime appointmentDate,
        String status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
