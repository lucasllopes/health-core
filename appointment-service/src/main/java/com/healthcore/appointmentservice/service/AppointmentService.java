package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.CreateAppointmentRequestDTO;
import com.healthcore.appointmentservice.dto.AppointmentRegistrationDTO;
import com.healthcore.appointmentservice.dto.AppointmentRequestDTO;
import com.healthcore.appointmentservice.dto.AppointmentResponseDTO;
import com.healthcore.appointmentservice.dto.AppointmentUpdateDTO;
import com.healthcore.appointmentservice.dto.message.AppointmentNotificationDTO;
import com.healthcore.appointmentservice.exception.DataNotFoundException;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import com.healthcore.appointmentservice.persistence.repository.DoctorRepository;
import com.healthcore.appointmentservice.persistence.repository.NurseRepository;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import com.healthcore.appointmentservice.producer.AppointmentProducerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final AppointmentProducerService appointmentProducerService;

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

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
                .orElseThrow(() -> new DataNotFoundException("Paciente não encontrado: id=" + patientId));
    }

    private Doctor findDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DataNotFoundException("Médico não encontrado: id=" + doctorId));
    }

    private Nurse findNurseById(Long nurseId) {
        return nurseRepository.findById(nurseId)
                .orElseThrow(() -> new DataNotFoundException("Enfermeiro não encontrado: id=" + nurseId));
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

    public AppointmentResponseDTO createAppointment(AppointmentRegistrationDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setPatient(findPatientById(dto.patientId()));
        appointment.setDoctor(findDoctorById(dto.doctorId()));
        appointment.setNurse(dto.nurseId() != null ? findNurseById(dto.nurseId()) : null);
        appointment.setAppointmentDate(dto.appointmentDate());
        appointment.setStatus(dto.status());
        appointment.setNotes(dto.notes());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        return toResponseDTO(appointment);
    }

    public Page<AppointmentResponseDTO> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Agendamento não encontrado: id=" + id));
        return toResponseDTO(appointment);
    }

    public AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Agendamento não encontrado: id=" + id));
        if (dto.patientId() != null) appointment.setPatient(findPatientById(dto.patientId()));
        if (dto.doctorId() != null) appointment.setDoctor(findDoctorById(dto.doctorId()));
        if (dto.nurseId() != null) appointment.setNurse(findNurseById(dto.nurseId()));
        if (dto.appointmentDate() != null) appointment.setAppointmentDate(dto.appointmentDate());
        if (dto.status() != null) appointment.setStatus(dto.status());
        if (dto.notes() != null) appointment.setNotes(dto.notes());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        return toResponseDTO(appointment);
    }

    public AppointmentResponseDTO disableAppointment(Long id) {
        log.info("Desabilitando agendamento: id={}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Agendamento não encontrado: id=" + id));
        appointment.setStatus("DISABLED");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        log.info("Agendamento desabilitado com sucesso: id={}", id);
        return toResponseDTO(appointment);
    }

    public AppointmentResponseDTO enableAppointment(Long id) {
        log.info("Habilitando agendamento: id={}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Agendamento não encontrado: id=" + id));
        appointment.setStatus("ENABLED");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        log.info("Agendamento habilitado com sucesso: id={}", id);
        return toResponseDTO(appointment);
    }

    public void deleteAppointment(Long id) {
        log.info("Excluindo agendamento: id={}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Agendamento não encontrado: id=" + id));
        appointmentRepository.delete(appointment);
        log.info("Agendamento excluído com sucesso: id={}", id);
    }

    public List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId) {
        log.info("Buscando agendamentos do paciente: id={}", patientId);
        Patient patient = findPatientById(patientId);
        List<AppointmentResponseDTO> result = appointmentRepository.findAll(Pageable.unpaged())
                .stream()
                .filter(a -> a.getPatient().getId().equals(patientId))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        log.info("{} agendamentos encontrados para o paciente: id={}", result.size(), patientId);
        return result;
    }

    public List<AppointmentResponseDTO> getFutureAppointmentsByPatient(Long patientId) {
        log.info("Buscando agendamentos futuros do paciente: id={}", patientId);
        Patient patient = findPatientById(patientId);
        LocalDateTime now = LocalDateTime.now();
        List<AppointmentResponseDTO> result = appointmentRepository.findAll(Pageable.unpaged())
                .stream()
                .filter(a -> a.getPatient().getId().equals(patientId) && a.getAppointmentDate().isAfter(now))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        log.info("{} agendamentos futuros encontrados para o paciente: id={}", result.size(), patientId);
        return result;
    }

    private AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getDoctor().getId(),
                appointment.getNurse() != null ? appointment.getNurse().getId() : null,
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt(),
                appointment.getMedicalRecord() != null ? appointment.getMedicalRecord().getId() : null
        );
    }
}
