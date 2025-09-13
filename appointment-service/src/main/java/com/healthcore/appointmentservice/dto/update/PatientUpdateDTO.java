package com.healthcore.appointmentservice.dto.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientUpdateDTO(
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String name,

        LocalDate dateOfBirth,

        @Size(max = 50, message = "Documento deve ter no máximo 50 caracteres")
        String document,

        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String phone,

        @Email(message = "Email deve ter formato válido")
        @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
        String email,

        String address
) {}
