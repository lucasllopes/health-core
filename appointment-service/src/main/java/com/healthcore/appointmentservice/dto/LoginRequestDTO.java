package com.healthcore.appointmentservice.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O campo Login é obrigatório") String login,
        @NotBlank(message = "O campo Senha é obrigatório") String password
) {
}
