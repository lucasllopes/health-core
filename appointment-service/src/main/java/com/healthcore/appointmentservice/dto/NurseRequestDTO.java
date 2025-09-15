package com.healthcore.appointmentservice.dto;
public record NurseRequestDTO(
        Long id,
        Long userId,
        String name,
        String coren
) {
}