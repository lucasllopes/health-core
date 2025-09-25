package com.healthcore.appointmentservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateAppointmentRequestDTO(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        Long nurseId,
        @NotNull @Future LocalDateTime appointmentDate,
        String notes
) {}

