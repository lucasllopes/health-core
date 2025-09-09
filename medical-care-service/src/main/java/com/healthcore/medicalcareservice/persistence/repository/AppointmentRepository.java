package com.healthcore.medicalcareservice.persistence.repository;

import com.healthcore.medicalcareservice.persistence.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByNameIgnoreCase(String name);
}
