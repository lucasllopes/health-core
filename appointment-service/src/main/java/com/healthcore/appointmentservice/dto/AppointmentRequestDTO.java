package com.healthcore.appointmentservice.dto;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(
    Long patientId,
    Long doctorId,
    Long nurseId,
    LocalDateTime appointmentDate,
    String status,
    String notes
) {}
