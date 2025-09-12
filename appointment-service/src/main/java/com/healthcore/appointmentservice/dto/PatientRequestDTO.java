package com.healthcore.appointmentservice.dto;

import java.time.LocalDate;
public record PatientRequestDTO(
        Long id,
        Long userId,
        String name,
        LocalDate dateOfBirth,
        String document,
        String phone,
        String email,
        String address
) {
}