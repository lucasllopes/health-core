package com.healthcore.appointmentservice.mapper;

import com.healthcore.appointmentservice.dto.*;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.persistence.entity.MedicalRecord;

import java.time.LocalDateTime;

public class AppointmentDTOConverter {

    public static AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getPatient() != null ? appointment.getPatient().getId() : null,
                appointment.getDoctor() != null ? appointment.getDoctor().getId() : null,
                appointment.getNurse() != null ? appointment.getNurse().getId() : null,
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt(),
                appointment.getMedicalRecord() != null ? appointment.getMedicalRecord().getId() : null
        );
    }

    public static Appointment fromRegistrationDTO(AppointmentRegistrationDTO dto, Patient patient, Doctor doctor, Nurse nurse) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setNurse(nurse);
        appointment.setAppointmentDate(dto.appointmentDate());
        appointment.setStatus(dto.status());
        appointment.setNotes(dto.notes());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointment;
    }

    public static void updateFromUpdateDTO(Appointment appointment, AppointmentUpdateDTO dto, Nurse nurse) {
        if (dto.nurseId() != null) appointment.setNurse(nurse);
        if (dto.appointmentDate() != null) appointment.setAppointmentDate(dto.appointmentDate());
        if (dto.status() != null) appointment.setStatus(dto.status());
        if (dto.notes() != null) appointment.setNotes(dto.notes());
        appointment.setUpdatedAt(LocalDateTime.now());
    }

    public static void updateFromRequestDTO(Appointment appointment, AppointmentRequestDTO dto, Patient patient, Doctor doctor, Nurse nurse) {
        if (dto.patientId() != null) appointment.setPatient(patient);
        if (dto.doctorId() != null) appointment.setDoctor(doctor);
        if (dto.nurseId() != null) appointment.setNurse(nurse);
        if (dto.appointmentDate() != null) appointment.setAppointmentDate(dto.appointmentDate());
        if (dto.status() != null) appointment.setStatus(dto.status());
        if (dto.notes() != null) appointment.setNotes(dto.notes());
        appointment.setUpdatedAt(LocalDateTime.now());
    }
}

