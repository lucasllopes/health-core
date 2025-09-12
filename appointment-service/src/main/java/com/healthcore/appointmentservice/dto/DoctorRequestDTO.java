package com.healthcore.appointmentservice.dto;
public record DoctorRequestDTO(
        Long id,
        Long userId,
        String name,
        String specialty,
        String crm
) {
}