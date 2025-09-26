package com.healthcore.appointmentservice.persistence.repository;

import com.healthcore.appointmentservice.persistence.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
}
