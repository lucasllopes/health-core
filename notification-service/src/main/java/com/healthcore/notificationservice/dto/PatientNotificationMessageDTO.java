package com.healthcore.notificationservice.dto;

public record PatientNotificationMessageDTO(
        String name,
        String document,
        String phone,
        String email
) {
}
