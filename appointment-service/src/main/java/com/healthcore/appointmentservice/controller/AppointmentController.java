package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.CreateAppointmentRequestDTO;
import com.healthcore.appointmentservice.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateAppointmentRequestDTO createAppointmentRequestDTO) {
        logger.info("Handling POST request to /usuarios");
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(createAppointmentRequestDTO));
    }

}
