package com.healthcore.appointmentservice.persistence.repository;

import com.healthcore.appointmentservice.persistence.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findById(Long id);

    Page<Appointment> findAll(Pageable pageable);

    Page<Appointment> findByPatient_Document(String document, Pageable pageable);

    Page<Appointment> findByDoctor_Crm(String crm, Pageable pageable);

    Page<Appointment> findByDoctor_CrmAndAppointmentDateGreaterThanEqual(String crm, LocalDateTime date, Pageable pageable);

    Page<Appointment> findByPatient_DocumentAndAppointmentDateGreaterThanEqual(String document, LocalDateTime date, Pageable pageable);

    Page<Appointment> findByAppointmentDateGreaterThanEqual(LocalDateTime date, Pageable pageable);

    Page<Appointment> findByPatient_DocumentAndAppointmentDateLessThan(String document, LocalDateTime to, Pageable p);

    Page<Appointment> findByDoctor_CrmAndAppointmentDateLessThan(String crm, LocalDateTime to, Pageable p);

    Page<Appointment> findByAppointmentDateLessThan(LocalDateTime to, Pageable p);

}
