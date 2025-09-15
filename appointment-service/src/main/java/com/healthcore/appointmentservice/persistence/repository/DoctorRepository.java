package com.healthcore.appointmentservice.persistence.repository;

import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBynameIgnoreCase(String name);
}
