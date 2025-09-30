package com.healthcore.appointmentservice.persistence.repository;

import com.healthcore.appointmentservice.persistence.entity.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Long> {

    @Query(value = "SELECT n.* FROM nurses n " +
            "JOIN users u ON u.id = n.user_id " +
            "WHERE (:name IS NULL OR UPPER(n.name) LIKE UPPER(CONCAT('%', :name, '%'))) " +
            "AND (:coren IS NULL OR UPPER(n.coren) LIKE UPPER(CONCAT('%', :coren, '%'))) " +
            "AND u.enabled = true",
            nativeQuery = true)
    List<Nurse> findActiveNursesByFilters(@Param("name") String name, @Param("coren") String coren);

    Optional<Nurse> findByUser_UsernameIgnoreCase(String username);
}
