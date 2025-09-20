package com.healthcore.appointmentservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record AppointmentRegistrationDTO(
    @NotNull Long patientId,
    @NotNull Long doctorId,
    Long nurseId,
    @NotNull @FutureOrPresent LocalDateTime appointmentDate,
    @NotNull @Size(min = 2, max = 30) String status,
    @Size(max = 1000) String notes
) {}
