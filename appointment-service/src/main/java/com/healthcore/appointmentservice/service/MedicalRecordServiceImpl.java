package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.CreateMedicalRecordRequestDTO;
import com.healthcore.appointmentservice.dto.UpdateMedicalRecordRequestDTO;
import com.healthcore.appointmentservice.dto.MedicalRecordResponseDTO;
import com.healthcore.appointmentservice.persistence.entity.MedicalRecord;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.persistence.repository.MedicalRecordRepository;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import com.healthcore.appointmentservice.persistence.repository.DoctorRepository;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import com.healthcore.appointmentservice.exception.MedicalRecordNotFoundException;
import com.healthcore.appointmentservice.exception.MedicalRecordValidationException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordServiceImpl.class);

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                   AppointmentRepository appointmentRepository,
                                   DoctorRepository doctorRepository,
                                   PatientRepository patientRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public MedicalRecordResponseDTO create(CreateMedicalRecordRequestDTO dto) {
        logger.info("Creating MedicalRecord for appointmentId={}, doctorId={}, patientId={}", dto.getAppointmentId(), dto.getDoctorId(), dto.getPatientId());

        Optional<MedicalRecord> existing = medicalRecordRepository.findByAppointmentId(dto.getAppointmentId());
        if (existing.isPresent()) {
            throw new MedicalRecordValidationException("Já existe um prontuário para este agendamento.");
        }

        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new MedicalRecordNotFoundException("Appointment not found: " + dto.getAppointmentId()));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new MedicalRecordNotFoundException("Doctor not found: " + dto.getDoctorId()));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new MedicalRecordNotFoundException("Patient not found: " + dto.getPatientId()));
        if (dto.getDiagnosis() == null || dto.getDiagnosis().trim().isEmpty()) {
            throw new MedicalRecordValidationException("Diagnosis is required");
        }
        MedicalRecord record = new MedicalRecord();
        record.setAppointment(appointment);
        record.setDoctor(doctor);
        record.setPatient(patient);
        record.setDiagnosis(dto.getDiagnosis());
        record.setPrescription(dto.getPrescription());
        record.setObservations(dto.getObservations());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        medicalRecordRepository.save(record);
        return toResponseDTO(record);
    }

    @Override
    public Page<MedicalRecordResponseDTO> getAll(Pageable pageable) {
        logger.info("Listing MedicalRecords with pageable: {}", pageable);
        return medicalRecordRepository.findAll(pageable).map(this::toResponseDTO);
    }

    @Override
    public MedicalRecordResponseDTO getById(Long id) {
        logger.info("Getting MedicalRecord by id={}", id);

        MedicalRecord medicalRecord = medicalRecordRepository
                    .findById(id)
                    .orElseThrow(() -> new MedicalRecordNotFoundException("Registro médico não encontrado: " + id));
        return toResponseDTO(medicalRecord);
    }

    @Override
    public MedicalRecordResponseDTO update(Long id, UpdateMedicalRecordRequestDTO dto) {
        logger.info("Updating MedicalRecord id={}", id);
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("MedicalRecord not found: " + id));
        if (dto.getDiagnosis() == null || dto.getDiagnosis().trim().isEmpty()) {
            throw new MedicalRecordValidationException("Diagnosis is required");
        }
        record.setDiagnosis(dto.getDiagnosis());
        record.setPrescription(dto.getPrescription());
        record.setObservations(dto.getObservations());
        record.setUpdatedAt(LocalDateTime.now());
        medicalRecordRepository.save(record);
        return toResponseDTO(record);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting MedicalRecord id={}", id);
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("MedicalRecord not found: " + id));

        Appointment appointment = record.getAppointment();
        if (appointment != null) {
            appointment.setMedicalRecord(null);
            appointmentRepository.save(appointment);
        }
        medicalRecordRepository.delete(record);
    }

    @Override
    public boolean isPatientOwner(String username, Long medicalRecordId) {
        // Busca o MedicalRecord pelo id
        return medicalRecordRepository.findById(medicalRecordId)
                .map(record -> {
                    if (record.getPatient() == null) return false;
                    return username.equalsIgnoreCase(record.getPatient().getEmail());
                })
                .orElse(false);
    }

    private MedicalRecordResponseDTO toResponseDTO(MedicalRecord record) {
        MedicalRecordResponseDTO dto = new MedicalRecordResponseDTO();
        dto.setId(record.getId());
        dto.setAppointmentId(record.getAppointment().getId());
        dto.setDoctorId(record.getDoctor().getId());
        dto.setPatientId(record.getPatient().getId());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setPrescription(record.getPrescription());
        dto.setObservations(record.getObservations());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }
}
