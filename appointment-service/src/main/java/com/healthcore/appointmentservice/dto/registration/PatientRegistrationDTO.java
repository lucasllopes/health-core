package com.healthcore.appointmentservice.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRegistrationDTO(
        @NotBlank(message = "Username é obrigatório")
        @Size(max = 50, message = "Username deve ter no máximo 50 caracteres")
        String username,

        @NotBlank(message = "Password é obrigatório")
        @Size(min = 6, message = "Password deve ter no mínimo 6 caracteres")
        String password,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String name,

        LocalDate dateOfBirth,

        @NotBlank(message = "Documento é obrigatório")
        @Size(max = 50, message = "Documento deve ter no máximo 50 caracteres")
        String document,

        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String phone,

        @Email(message = "Email deve ter formato válido")
        @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
        String email,

        String address
) {}