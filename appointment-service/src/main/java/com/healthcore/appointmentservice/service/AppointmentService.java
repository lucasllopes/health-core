package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.*;
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
import com.healthcore.appointmentservice.producer.NotificationMessageSender;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final NotificationMessageSender notificationMessageSender;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              NurseRepository nurseRepository,
                              NotificationMessageSender notificationMessageSender) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.notificationMessageSender = notificationMessageSender;
    }

    public AppointmentResponseDTO create(CreateAppointmentRequestDTO createAppointmentRequestDTO) {
        Appointment appointment = buildAppointment(createAppointmentRequestDTO);
        appointment.setStatus(createAppointmentRequestDTO.status());
        appointmentRepository.save(appointment);
        // Envio para fila de notificação
        AppointmentNotificationMessageDTO notificationMsg = toNotificationMessageDTO(appointment);
        notificationMessageSender.sendNotification(notificationMsg);
        return toResponseDTO(appointment);
    }

    public Page<AppointmentResponseDTO> getAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(this::toResponseDTO);
    }

    public Optional<AppointmentResponseDTO> getById(Long id) {
        return appointmentRepository.findById(id).map(this::toResponseDTO);
    }

    public AppointmentResponseDTO update(Long appointmentId, UpdateAppointmentRequestDTO updateRequest) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        if (updateRequest.getPatientId() != null) {
            existingAppointment.setPatient(findPatientById(updateRequest.getPatientId()));
        }
        if (updateRequest.getDoctorId() != null) {
            existingAppointment.setDoctor(findDoctorById(updateRequest.getDoctorId()));
        }
        if (updateRequest.getAppointmentDate() != null) {
            existingAppointment.setAppointmentDate(updateRequest.getAppointmentDate());
        }
        if (updateRequest.getStatus() != null) {
            existingAppointment.setStatus(updateRequest.getStatus());
        }
        if (updateRequest.getNotes() != null) {
            existingAppointment.setNotes(updateRequest.getNotes());
        }
        existingAppointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(existingAppointment);
        // Envio para fila de notificação
        AppointmentNotificationMessageDTO notificationMsg = toNotificationMessageDTO(existingAppointment);
        notificationMessageSender.sendNotification(notificationMsg);
        return toResponseDTO(existingAppointment);
    }

    public void delete(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + id));
        appointmentRepository.delete(appointment);
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
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }

    private AppointmentNotificationMessageDTO toNotificationMessageDTO(Appointment appointment) {
        AppointmentNotificationMessageDTO dto = new AppointmentNotificationMessageDTO();
        dto.setAppointmentId(appointment.getId());

        dto.setPatient(
                new PatientNotificationMessageDTO(
                        appointment.getPatient().getName(),
                        appointment.getPatient().getDocument(),
                        appointment.getPatient().getPhone(),
                        appointment.getPatient().getEmail()));

        dto.setDoctor(
                new DoctorNotificationMessageDTO(
                        appointment.getDoctor().getName(),
                        appointment.getDoctor().getSpecialty(),
                        appointment.getDoctor().getCrm()));

        dto.setNurse(
                new NurseNotifocationMessageDTO(
                        appointment.getNurse().getName(),
                        appointment.getNurse().getCoren()));

        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        return dto;
    }

    private AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setDoctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null);
        dto.setPatientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null);
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        return dto;
    }

    public boolean isPatientOwner(String username, Long appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) return false;
        Appointment appointment = appointmentOpt.get();
        Patient patient = appointment.getPatient();
        if (patient == null) return false;
        return username.equalsIgnoreCase(patient.getEmail());
    }
}
