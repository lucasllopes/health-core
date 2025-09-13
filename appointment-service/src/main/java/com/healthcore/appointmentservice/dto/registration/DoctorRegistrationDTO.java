package com.healthcore.appointmentservice.dto.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoctorRegistrationDTO(
        @NotBlank(message = "Username é obrigatório")
        @Size(max = 50, message = "Username deve ter no máximo 50 caracteres")
        String username,

        @NotBlank(message = "Password é obrigatório")
        @Size(min = 6, message = "Password deve ter no mínimo 6 caracteres")
        String password,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String name,

        @Size(max = 100, message = "Especialidade deve ter no máximo 100 caracteres")
        String specialty,

        @NotBlank(message = "CRM é obrigatório")
        @Size(max = 30, message = "CRM deve ter no máximo 30 caracteres")
        String crm
) {}