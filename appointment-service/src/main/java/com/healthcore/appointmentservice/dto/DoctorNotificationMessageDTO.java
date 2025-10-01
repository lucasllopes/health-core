package com.healthcore.appointmentservice.dto;

public record DoctorNotificationMessageDTO(
        String name,
        String specialty,
        String crm) {
}
