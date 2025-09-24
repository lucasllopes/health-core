package com.healthcore.appointmentservice.persistence.repository;

import com.healthcore.appointmentservice.persistence.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findById(Long id);

    Page<Appointment> findAll(Pageable pageable);

    Page<Appointment> findByPatient_Document(String document, Pageable pageable);

    Page<Appointment> findByDoctor_Crm(String crm, Pageable pageable);

    Page<Appointment> findByPatient_DocumentAndAppointmentDateGreaterThanEqualAndStatus(String doc, LocalDateTime now, String statusPendente, Pageable pageable);

    Page<Appointment> findByPatient_DocumentAndAppointmentDateLessThanAndStatus(String doc, LocalDateTime now, String statusConcluido, Pageable pageable);

    Page<Appointment> findByDoctor_CrmAndAppointmentDateGreaterThanEqualAndStatus(String crm, LocalDateTime now, String statusPendente, Pageable pageable);

    Page<Appointment> findByDoctor_CrmAndAppointmentDateLessThanAndStatus(String crm, LocalDateTime now, String statusConcluido, Pageable pageable);

    Page<Appointment> findByAppointmentDateGreaterThanEqualAndStatus(LocalDateTime now, String statusPendente, Pageable pageable);

    Page<Appointment> findByAppointmentDateLessThanAndStatus(LocalDateTime now, String statusConcluido, Pageable pageable);

    List<Appointment> findByAppointmentDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);
}
