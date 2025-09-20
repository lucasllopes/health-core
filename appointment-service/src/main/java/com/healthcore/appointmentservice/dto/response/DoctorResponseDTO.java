package com.healthcore.appointmentservice.dto.response;

import com.healthcore.appointmentservice.persistence.entity.Doctor;

public record DoctorResponseDTO(
        Long id,
        Long userId,
        String name,
        String specialty,
        String crm
) {

    public static DoctorResponseDTO fromEntity(Doctor doctor) {
        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getUser().getId(),
                doctor.getName(),
                doctor.getSpecialty(),
                doctor.getCrm()
        );
    }
}