package com.healthcore.appointmentservice.dto.message;

import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.entity.Patient;

import java.time.LocalDateTime;

public record UpcomingAppointementNotificationDTO(Long id,
                                                  Patient patient,
                                                  Doctor doctor,
                                                  Nurse nurse,
                                                  LocalDateTime appointmentDate,
                                                  String status,
                                                  String notes,
                                                  LocalDateTime createdAt,
                                                  LocalDateTime updatedAt) {
}
