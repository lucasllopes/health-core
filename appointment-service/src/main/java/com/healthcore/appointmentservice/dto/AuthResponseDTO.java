package com.healthcore.appointmentservice.dto;

public record AuthResponseDTO(
        String acessToken,
        String refreshToken
) {
}
