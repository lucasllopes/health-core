package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.graphql.AppointmentFilterInput;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import com.healthcore.appointmentservice.persistence.repository.DoctorRepository;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("authValidationService")
public class AuthorizationService {

    private final UserService userService;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final AppointmentRepository appointmentRepo;

    public AuthorizationService(UserService userService,
                                PatientRepository patientRepo,
                                DoctorRepository doctorRepo,
                                AppointmentRepository appointmentRepo) {
        this.userService = userService;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.appointmentRepo = appointmentRepo;
    }

    public boolean canSearchAppointments(Authentication auth,
                                         AppointmentFilterInput filter) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        var roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(java.util.stream.Collectors.toSet());

        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_NURSE") || roles.contains("ROLE_DOCTOR")) {
            return true;
        }

        if (roles.contains("ROLE_PATIENT")) {
            if (filter == null) {
                return false;
            }
            if (hasText(filter.doctorCrm())) {
                return false;
            }

            if (!hasText(filter.patientDocument())) {
                return false;
            }

            var user = userService.findByUsername(auth.getName()).orElse(null);
            if (user == null) {
                return false;
            }

            var patient = patientRepo.findByUser_Id(user.getId()).orElse(null);

            if (patient == null) {
                return false;
            }

            String docUser = patient.getDocument();
            String docReq = filter.patientDocument();
            return docReq.equals(docUser);
        }
        return false;
    }

    public boolean canViewAppointment(Authentication auth, Long apptId) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        var roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(java.util.stream.Collectors.toSet());

        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_NURSE")) {
            return true;
        }

        var user = userService.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return false;
        }

        var appointment = appointmentRepo.findById(apptId).orElse(null);

        if (appointment == null) {
            return false;
        }

        if (roles.contains("ROLE_DOCTOR")) {
            var doctor = doctorRepo.findByUser_Id(user.getId()).orElse(null);
            return doctor != null &&
                    appointment.getDoctor() != null &&
                    safeEq(appointment.getDoctor().getCrm(), doctor.getCrm());
        }

        if (roles.contains("ROLE_PATIENT")) {
            var patient = patientRepo.findByUser_Id(user.getId()).orElse(null);
            return patient != null &&
                    appointment.getPatient() != null &&
                    safeEq((appointment.getPatient().getDocument()), patient.getDocument());
        }

        return false;
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static boolean safeEq(String a, String b) {
        return java.util.Objects.equals(a, b);
    }
}
