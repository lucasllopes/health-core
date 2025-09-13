package com.healthcore.appointmentservice.persistence.repository;

import com.healthcore.appointmentservice.persistence.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findBynameIgnoreCase(String name);

    Optional<Patient> findByDocument(String document);

    @Query("SELECT p FROM Patient p WHERE p.user.id = :userId")
    Optional<Patient> findByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Patient p WHERE p.email = :email")
    Optional<Patient> findByEmail(@Param("email") String email);

    @Query("SELECT p FROM Patient p WHERE p.name LIKE %:name%")
    List<Patient> findByNameContaining(@Param("name") String name);

    @Query("SELECT p FROM Patient p WHERE p.user.enabled = true")
    List<Patient> findAllActivePatients();

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.user.enabled = true")
    long countActivePatients();
}
