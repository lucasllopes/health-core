package com.healthcore.medicalcareservice.controller;

import com.healthcore.medicalcareservice.persistence.entity.Appointment;
import com.healthcore.medicalcareservice.service.AppointmentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AppointmentGraphQLController {

    private final AppointmentService appointmentService;

    public AppointmentGraphQLController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @QueryMapping
    public List<Appointment> allAppointments() {
        return appointmentService.findAll();
    }

    @QueryMapping
    public List<Appointment> appointmentByName(@Argument String name) {
        return appointmentService.findByNameIgnoreCase(name);
    }
}
