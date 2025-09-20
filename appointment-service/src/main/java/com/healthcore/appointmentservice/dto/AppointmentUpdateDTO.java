package com.healthcore.appointmentservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record AppointmentUpdateDTO(
    Long nurseId,
    @FutureOrPresent LocalDateTime appointmentDate,
    @Size(min = 2, max = 30) String status,
    @Size(max = 1000) String notes
) {}
