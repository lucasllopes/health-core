package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.graphql.AppointmentFilterInput;
import com.healthcore.appointmentservice.exception.ArgumentException;
import com.healthcore.appointmentservice.exception.DataNotFoundException;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.repository.DoctorRepository;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("authValidationService")
public class AuthorizationService {

    private final UserService userService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AuthorizationService(UserService userService, PatientRepository patientRepo,
                                DoctorRepository doctorRepo) {
        this.userService = userService;
        this.patientRepository = patientRepo;
        this.doctorRepository = doctorRepo;
    }

    public boolean canSearchAppointments(Authentication auth, AppointmentFilterInput filter) {
        if (auth == null || !auth.isAuthenticated()) return false;
        var roles = roles(auth);

        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_NURSE") || roles.contains("ROLE_DOCTOR")) {
            return true;
        }

        if (roles.contains("ROLE_PATIENT")) {
            if (filter == null || !hasText(filter.patientDocument())){
                throw new ArgumentException("Informe o seu documento para buscar seus agendamentos.");
            }
            if (hasText(filter.doctorCrm())) {
                throw new ArgumentException("Informe o seu documento e não o CRM.");
            }

            var user = userService.findByUsername(auth.getName()).orElse(null);
            if (user == null) {
                throw new DataNotFoundException("Informe um token/usuário válido.");
            }

            var patient = patientRepository.findByUser_Id(user.getId()).orElse(null);
            if (patient == null) {
                throw new DataNotFoundException("Usuário não é um paciente válido.");
            }

            if (!filter.patientDocument().equals(patient.getDocument())){
                throw new ArgumentException("Usuário não coincide com o documento informado.");
            }

            return true;
        }

        return false;
    }

    public boolean canViewAppointmentEntity(Authentication auth, Appointment appt) {
        if (auth == null || !auth.isAuthenticated()) return false;
        var roles = roles(auth);

        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_NURSE") || roles.contains("ROLE_DOCTOR")) return true;

        var user = userService.findByUsername(auth.getName()).orElse(null);
        if (user == null) return false;

        if (roles.contains("ROLE_PATIENT")) {
            var patient = patientRepository.findByUser_Id(user.getId()).orElse(null);
            return patient != null && appt.getPatient() != null &&
                    appt.getPatient().getDocument().equals(patient.getDocument());
        }

        return false;
    }

    private static java.util.Set<String> roles(Authentication auth) {
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(java.util.stream.Collectors.toSet());
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static boolean safeEq(String a, String b) {
        return java.util.Objects.equals(a, b);
    }
}
