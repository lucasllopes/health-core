package com.healthcore.appointmentservice.dto;

import java.time.LocalDateTime;

public record AppointmentUpdateDTO(
    Long nurseId,
    LocalDateTime appointmentDate,
    String status,
    String notes
) {}
