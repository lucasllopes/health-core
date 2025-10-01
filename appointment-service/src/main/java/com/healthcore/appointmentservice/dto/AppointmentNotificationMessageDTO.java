package com.healthcore.appointmentservice.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AppointmentNotificationMessageDTO implements Serializable {
    private Long appointmentId;
    private PatientNotificationMessageDTO patient;
    private DoctorNotificationMessageDTO doctor;
    private NurseNotifocationMessageDTO nurse;
    private LocalDateTime appointmentDate;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public PatientNotificationMessageDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientNotificationMessageDTO patient) {
        this.patient = patient;
    }

    public DoctorNotificationMessageDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorNotificationMessageDTO doctor) {
        this.doctor = doctor;
    }

    public NurseNotifocationMessageDTO getNurse() {
        return nurse;
    }

    public void setNurse(NurseNotifocationMessageDTO nurse) {
        this.nurse = nurse;
    }
}