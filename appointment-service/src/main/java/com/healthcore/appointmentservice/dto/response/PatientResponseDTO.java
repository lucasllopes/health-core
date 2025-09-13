package com.healthcore.appointmentservice.dto.response;

import com.healthcore.appointmentservice.persistence.entity.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponseDTO(
        Long id,
        Long userId,
        String username,
        String name,
        LocalDate dateOfBirth,
        String document,
        String phone,
        String email,
        String address,
        Boolean enabled,
        LocalDateTime createdAt
) {
    public static PatientResponseDTO fromEntity(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getUser().getId(),
                patient.getUser().getUsername(),
                patient.getName(),
                patient.getDateOfBirth(),
                patient.getDocument(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getUser().getEnabled(),
                patient.getUser().getCreatedAt()
        );
    }
}