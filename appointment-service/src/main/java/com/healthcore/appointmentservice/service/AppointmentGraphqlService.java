package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.graphql.AppointmentFilterInput;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class AppointmentGraphqlService {

    private static final ZoneId APP_ZONE = ZoneId.of("America/Sao_Paulo");


    private final AppointmentRepository appointmentRepository;

    public AppointmentGraphqlService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public Page<Appointment> findAllAppointments(AppointmentFilterInput filter, Pageable pageable) {

        String doc = trimToNull(filter != null ? filter.patientDocument() : null);
        String crm = trimToNull(filter != null ? filter.doctorCrm() : null);
        Boolean futureOnly = (filter != null) ? filter.futureOnly() : null;

        if (doc != null && crm != null) {
            throw new IllegalArgumentException("Use patientDocument OU doctorCrm, não ambos.");
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

        if (doc != null) {
            if (Boolean.TRUE.equals(futureOnly)) {
                return appointmentRepository.findByPatient_DocumentAndAppointmentDateGreaterThanEqual(doc, now, pageable);
            } else if (Boolean.FALSE.equals(futureOnly)) {
                return appointmentRepository.findByPatient_DocumentAndAppointmentDateLessThan(doc, now, pageable);
            } else {
                return appointmentRepository.findByPatient_Document(doc, pageable);
            }
        }

        if (crm != null) {
            if (Boolean.TRUE.equals(futureOnly)) {
                return appointmentRepository.findByDoctor_CrmAndAppointmentDateGreaterThanEqual(crm, now, pageable);
            } else if (Boolean.FALSE.equals(futureOnly)) {
                return appointmentRepository.findByDoctor_CrmAndAppointmentDateLessThan(crm, now, pageable);
            } else {
                return appointmentRepository.findByDoctor_Crm(crm, pageable);
            }
        }

        if (Boolean.TRUE.equals(futureOnly)) {
            return appointmentRepository.findByAppointmentDateGreaterThanEqual(now, pageable);
        } else if (Boolean.FALSE.equals(futureOnly)) {
            return appointmentRepository.findByAppointmentDateLessThan(now, pageable);
        } else {
            return appointmentRepository.findAll(pageable);
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

}

