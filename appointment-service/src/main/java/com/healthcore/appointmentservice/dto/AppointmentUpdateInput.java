package com.healthcore.appointmentservice.dto;

import java.time.LocalDateTime;

public record AppointmentUpdateInput(
    Long nurseId,
    LocalDateTime appointmentDate,
    String status,
    String notes
) {}

