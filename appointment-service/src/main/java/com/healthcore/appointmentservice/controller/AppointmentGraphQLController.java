package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.service.AppointmentGraphqlService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AppointmentGraphQLController {

    private final AppointmentGraphqlService appointmentGraphqlService;

    public AppointmentGraphQLController(AppointmentGraphqlService appointmentGraphqlService) {
        this.appointmentGraphqlService = appointmentGraphqlService;
    }

    @QueryMapping
    public List<Appointment> allAppointments() {
        return appointmentGraphqlService.findAll();
    }

    @QueryMapping
    public Appointment appointmentById(@Argument Long id) {
        return appointmentGraphqlService.findById(id);
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
