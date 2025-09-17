package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.graphql.AppointmentFilterInput;
import com.healthcore.appointmentservice.dto.graphql.AppointmentPageGraphql;
import com.healthcore.appointmentservice.pagination.PageInput;
import com.healthcore.appointmentservice.pagination.PageOutput;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.service.AppointmentGraphqlService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class AppointmentGraphQLController {

    private final AppointmentGraphqlService appointmentGraphqlService;

    public AppointmentGraphQLController(AppointmentGraphqlService appointmentGraphqlService) {
        this.appointmentGraphqlService = appointmentGraphqlService;
    }

    @QueryMapping
    public Appointment appointmentById(@Argument Long id) {
        return appointmentGraphqlService.findById(id);
    }

    @PreAuthorize("@authValidationService.canSearchAppointments(authentication, #filter)")
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
}
