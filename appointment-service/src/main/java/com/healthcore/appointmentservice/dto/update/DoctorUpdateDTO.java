package com.healthcore.appointmentservice.dto.update;

import jakarta.validation.constraints.Size;

public record DoctorUpdateDTO (

    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    String name,

    @Size(max = 100, message = "Especialidade deve ter no máximo 100 caracteres")
    String specialty,

    @Size(max = 30, message = "Especialidade deve ter no máximo 30 caracteres")
    String crm
) {}
