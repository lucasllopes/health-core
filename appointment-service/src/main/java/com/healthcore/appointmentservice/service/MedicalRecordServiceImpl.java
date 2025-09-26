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
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

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
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + dto.getAppointmentId()));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + dto.getDoctorId()));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + dto.getPatientId()));
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
    public Optional<MedicalRecordResponseDTO> getById(Long id) {
        logger.info("Getting MedicalRecord by id={}", id);
        return medicalRecordRepository.findById(id).map(this::toResponseDTO);
    }

    @Override
    public MedicalRecordResponseDTO update(Long id, UpdateMedicalRecordRequestDTO dto) {
        logger.info("Updating MedicalRecord id={}", id);
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MedicalRecord not found: " + id));
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
                .orElseThrow(() -> new IllegalArgumentException("MedicalRecord not found: " + id));
        medicalRecordRepository.delete(record);
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

