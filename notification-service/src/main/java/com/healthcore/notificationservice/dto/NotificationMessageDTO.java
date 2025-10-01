package com.healthcore.notificationservice.dto;

import java.time.LocalDateTime;

public record NotificationMessageDTO(
        Long appointmentId,
        PatientNotificationMessageDTO patient,
        DoctorNotificationMessageDTO doctor,
        NurseNotificationMessageDTO nurse,
        LocalDateTime appointmentDate,
        String status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
