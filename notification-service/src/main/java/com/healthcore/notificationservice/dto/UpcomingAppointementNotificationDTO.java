package com.healthcore.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpcomingAppointementNotificationDTO(Long id,
                                                  PatientDTO patient,
                                                  DoctorDTO doctor,
                                                  NurseDTO nurse,
                                                  LocalDateTime appointmentDate,
                                                  String status,
                                                  String notes,
                                                  LocalDateTime createdAt,
                                                  LocalDateTime updatedAt) {
}
