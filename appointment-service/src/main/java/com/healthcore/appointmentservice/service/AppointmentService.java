package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.CreateAppointmentRequestDTO;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public String create(CreateAppointmentRequestDTO createAppointmentRequestDTO) {
        var appointment = new Appointment();
        appointment.setDescription(createAppointmentRequestDTO.description());
        appointment.setName(createAppointmentRequestDTO.name());
        appointmentRepository.save(appointment);
        return "Appointment id: " + appointment.getId();
    }
}
