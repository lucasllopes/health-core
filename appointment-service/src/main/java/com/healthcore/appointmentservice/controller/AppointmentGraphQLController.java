package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.*;
import com.healthcore.appointmentservice.dto.graphql.AppointmentFilterInput;
import com.healthcore.appointmentservice.dto.graphql.AppointmentPageGraphql;
import com.healthcore.appointmentservice.exception.AccessDeniedException;
import com.healthcore.appointmentservice.exception.DataNotFoundException;
import com.healthcore.appointmentservice.pagination.PageInput;
import com.healthcore.appointmentservice.pagination.PageOutput;
import com.healthcore.appointmentservice.persistence.entity.*;
import com.healthcore.appointmentservice.service.AppointmentGraphqlService;
import com.healthcore.appointmentservice.service.AppointmentService;
import com.healthcore.appointmentservice.service.AuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AppointmentGraphQLController {

    private final AppointmentGraphqlService appointmentGraphqlService;
    private final AuthorizationService authorizationService;
    private final AppointmentService appointmentService;

    public AppointmentGraphQLController(AppointmentGraphqlService appointmentGraphqlService, AuthorizationService authorizationService, AppointmentService appointmentService) {
        this.appointmentGraphqlService = appointmentGraphqlService;
        this.authorizationService = authorizationService;
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or (hasRole('PATIENT') and @authValidationService.isPatientOfAppointment(authentication, #id))")
    @QueryMapping
    public Appointment appointmentById(@Argument Long id, Authentication auth) {

        var appointment = appointmentGraphqlService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Appointment não encontrado: id=" + id));

        if (!authorizationService.canViewAppointmentEntity(auth, appointment)) {
            throw new AccessDeniedException("Você não tem acesso a este appointment");
        }

        return appointment;
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or (hasRole('PATIENT') and @authValidationService.isPatientOfFilter(authentication, #filter))")
    @QueryMapping
    public AppointmentPageGraphql appointments(
            @Argument(name = "filter") AppointmentFilterInput filter,
            @Argument(name = "page") PageInput page
    ) {
        int p = (page != null && page.page() != null) ? page.page() : 0;
        int s = (page != null && page.size() != null) ? page.size() : 20;

        Page<Appointment> result = appointmentGraphqlService.findAllAppointments(
                new AppointmentFilterInput(
                        filter != null ? filter.patientDocument() : null,
                        filter != null ? filter.doctorCrm() : null,
                        filter != null ? filter.futureOnly() : null
                ),
                PageRequest.of(p, s)
        );

        PageOutput infoPage = new PageOutput(
                result.getNumber(),
                result.getSize(),
                result.getTotalPages(),
                result.getTotalElements()
        );

        return new AppointmentPageGraphql(result.getContent(), infoPage);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByPatient(@Argument Long patientId) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    @QueryMapping
    public List<AppointmentResponseDTO> futureAppointmentsByPatient(@Argument Long patientId) {
        return appointmentService.getFutureAppointmentsByPatient(patientId);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    @MutationMapping
    public AppointmentResponseDTO createAppointment(@Argument AppointmentInput input) {
        return appointmentService.createAppointment(new AppointmentRegistrationDTO(
            input.patientId(),
            input.doctorId(),
            input.nurseId(),
            input.appointmentDate(),
            input.status(),
            input.notes()
        ));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    @MutationMapping
    public AppointmentResponseDTO updateAppointment(@Argument Long id, @Argument AppointmentUpdateInput input) {
        return appointmentService.updateAppointment(id, new AppointmentRequestDTO(
            null, // patientId não alterado
            null, // doctorId não alterado
            input.nurseId(),
            input.appointmentDate(),
            input.status(),
            input.notes()
        ));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    @MutationMapping
    public AppointmentResponseDTO disableAppointment(@Argument Long id) {
        return appointmentService.disableAppointment(id);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    @MutationMapping
    public AppointmentResponseDTO enableAppointment(@Argument Long id) {
        return appointmentService.enableAppointment(id);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    @MutationMapping
    public Boolean deleteAppointment(@Argument Long id) {
        appointmentService.deleteAppointment(id);
        return true;
    }

    @SchemaMapping(typeName = "Appointment", field = "patient")
    Patient patient(Appointment appointment) {
        return appointment.getPatient();
    }

    @SchemaMapping(typeName = "Appointment", field = "doctor")
    Doctor doctor(Appointment appointment) {
        return appointment.getDoctor();
    }

    @SchemaMapping(typeName = "Appointment", field = "nurse")
    Nurse nurse(Appointment appointment) {
        return appointment.getNurse();
    }

    @SchemaMapping(typeName = "Appointment", field = "medicalRecord")
    public MedicalRecord medicalRecord(Appointment a) {
        return a.getMedicalRecord();
    }

}
