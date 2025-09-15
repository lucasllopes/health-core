package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.service.AppointmentGraphqlService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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
    public Appointment appointmentByName(@Argument Long id) {
        return appointmentGraphqlService.findById(id);
    }
}
