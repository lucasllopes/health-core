package com.healthcore.appointmentservice.dto;

import java.time.LocalDateTime;
public record UserRequestDTO(
        Long id,
        String username,
        String password,
        String role,
        Boolean enabled,
        LocalDateTime createdAt
) {
}