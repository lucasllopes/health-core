package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.CreateAppointmentRequestDTO;
import com.healthcore.appointmentservice.dto.message.AppointmentNotificationDTO;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import com.healthcore.appointmentservice.persistence.repository.DoctorRepository;
import com.healthcore.appointmentservice.persistence.repository.NurseRepository;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import com.healthcore.appointmentservice.producer.AppointmentProducerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final AppointmentProducerService appointmentProducerService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentProducerService appointmentProducerService,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              NurseRepository nurseRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentProducerService = appointmentProducerService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
    }

    public String create(CreateAppointmentRequestDTO createAppointmentRequestDTO) {
        Appointment appointment = buildAppointment(createAppointmentRequestDTO);

        appointmentRepository.save(appointment);

        AppointmentNotificationDTO event = buildAppointmentNotification(appointment);
        appointmentProducerService.sendAppointmentCreated(event);

        return "Appointment id: " + appointment.getId();
    }

    private Appointment buildAppointment(CreateAppointmentRequestDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setPatient(findPatientById(dto.patientId()));
        appointment.setDoctor(findDoctorById(dto.doctorId()));
        appointment.setNurse(dto.nurseId() != null ? findNurseById(dto.nurseId()) : null);
        appointment.setAppointmentDate(dto.appointmentDate());
        appointment.setStatus(dto.status());
        appointment.setNotes(dto.notes());
        appointment.setCreatedAt(LocalDateTime.now());
        return appointment;
    }

    private Patient findPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + patientId));
    }

    private Doctor findDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + doctorId));
    }

    private Nurse findNurseById(Long nurseId) {
        return nurseRepository.findById(nurseId)
                .orElseThrow(() -> new IllegalArgumentException("Nurse not found with id: " + nurseId));
    }

    private AppointmentNotificationDTO buildAppointmentNotification(Appointment appointment) {
        return new AppointmentNotificationDTO(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getDoctor().getId(),
                appointment.getNurse() != null ? appointment.getNurse().getId() : null,
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
    public String update(Long appointmentId, CreateAppointmentRequestDTO updateRequest) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        if (updateRequest.patientId() != null) {
            existingAppointment.setPatient(findPatientById(updateRequest.patientId()));
        }
        if (updateRequest.doctorId() != null) {
            existingAppointment.setDoctor(findDoctorById(updateRequest.doctorId()));
        }
        if (updateRequest.nurseId() != null) {
            existingAppointment.setNurse(findNurseById(updateRequest.nurseId()));
        }
        if (updateRequest.appointmentDate() != null) {
            existingAppointment.setAppointmentDate(updateRequest.appointmentDate());
        }
        if (updateRequest.status() != null) {
            existingAppointment.setStatus(updateRequest.status());
        }
        if (updateRequest.notes() != null) {
            existingAppointment.setNotes(updateRequest.notes());
        }

        existingAppointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(existingAppointment);

        return "Appointment updated successfully with id: " + existingAppointment.getId();
    }
}
