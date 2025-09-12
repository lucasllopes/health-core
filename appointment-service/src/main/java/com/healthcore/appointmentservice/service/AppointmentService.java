package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.CreateAppointmentRequestDTO;
import com.healthcore.appointmentservice.dto.message.AppointmentNotificationDTO;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import com.healthcore.appointmentservice.persistence.repository.DoctorRepository;
import com.healthcore.appointmentservice.persistence.repository.NurseRepository;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import com.healthcore.appointmentservice.producer.AppointmentProducerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    AppointmentRepository appointmentRepository;
    PatientRepository patientRepository;
    DoctorRepository doctorRepository;
    NurseRepository nurseRepository;
    AppointmentProducerService appointmentProducerService;

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
        var appointment = new Appointment();
        validateRequest(createAppointmentRequestDTO, appointment);
        appointment.setAppointmentDate(createAppointmentRequestDTO.appointmentDate());
        appointment.setStatus(createAppointmentRequestDTO.status());
        appointment.setNotes(createAppointmentRequestDTO.notes());
        appointment.setCreatedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);

        AppointmentNotificationDTO event = new AppointmentNotificationDTO(
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

        appointmentProducerService.sendAppointmentCreated(event);

        return "Appointment id: " + appointment.getId();
    }

    private void validateRequest(CreateAppointmentRequestDTO createAppointmentRequestDTO, Appointment appointment) {
        appointment.setPatient(patientRepository.findById(createAppointmentRequestDTO.patientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found")));
        appointment.setDoctor(doctorRepository.findById(createAppointmentRequestDTO.doctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found")));
        appointment.setNurse(createAppointmentRequestDTO.nurseId() != null
                ? nurseRepository.findById(createAppointmentRequestDTO.nurseId())
                .orElse(null)
                : null);
    }
}
