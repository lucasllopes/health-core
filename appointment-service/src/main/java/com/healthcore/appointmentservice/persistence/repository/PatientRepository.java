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

    Optional<Patient> findByDocument(String document);

    @Query("SELECT p FROM Patient p WHERE p.user.id = :userId")
    Optional<Patient> findByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT p.* FROM patients p " +
            "JOIN users u ON u.id = p.user_id " +
            "WHERE (:name IS NULL OR UPPER(p.name) LIKE UPPER(CONCAT('%', :name, '%'))) " +
            "AND (:email IS NULL OR UPPER(p.email) LIKE UPPER(CONCAT('%', :email, '%'))) " +
            "AND (:document IS NULL OR p.document = :document) " +
            "AND u.enabled = true",
            nativeQuery = true)
    List<Patient> findActivePatientsByFilters(@Param("name") String name,
                                              @Param("email") String email,
                                              @Param("document") String document);

     Optional<Patient> findByUser_Id(Long userId);
    Optional<Patient> findByUser_UsernameIgnoreCase(String username);
}
