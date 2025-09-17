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

    private static final String STATUS_CONCLUIDO = "CONCLUIDO";

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

        LocalDateTime now = LocalDateTime.now();

        if (doc != null) {
            if (futureOnly == null) {
                return appointmentRepository.findByPatient_Document(doc, pageable);
            } else {
                if (futureOnly) {
                    return appointmentRepository.findByPatient_DocumentAndAppointmentDateGreaterThanEqualAndStatusNot(
                            doc, now, STATUS_CONCLUIDO, pageable);
                } else {
                    return appointmentRepository.findByPatient_DocumentAndAppointmentDateLessThan(
                            doc, now, pageable);
                }
            }
        }

        if (crm != null) {
            if (futureOnly == null) {
                return appointmentRepository.findByDoctor_Crm(crm, pageable);
            } else {
                if (futureOnly) {
                    return appointmentRepository.findByDoctor_CrmAndAppointmentDateGreaterThanEqualAndStatusNot(
                            crm, now, STATUS_CONCLUIDO, pageable);
                } else {
                    return appointmentRepository.findByDoctor_CrmAndAppointmentDateLessThan(
                            crm, now, pageable);
                }
            }
        }

        if (futureOnly == null) {
            return appointmentRepository.findAll(pageable);
        } else {
            if (futureOnly) {
                return appointmentRepository.findByAppointmentDateGreaterThanEqualAndStatusNot(
                        now, STATUS_CONCLUIDO, pageable);
            } else {
                return appointmentRepository.findByAppointmentDateLessThan(now, pageable);
            }
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}

