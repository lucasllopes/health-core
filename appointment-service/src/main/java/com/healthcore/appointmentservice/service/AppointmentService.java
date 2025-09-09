package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.CreateAppointmentRequestDTO;
import com.healthcore.appointmentservice.dto.message.AppointmentNotificationDTO;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import com.healthcore.appointmentservice.producer.AppointmentProducerService;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    AppointmentRepository appointmentRepository;
    AppointmentProducerService appointmentProducerService;

    public AppointmentService(AppointmentRepository appointmentRepository, AppointmentProducerService appointmentProducerService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentProducerService = appointmentProducerService;
    }

    public String create(CreateAppointmentRequestDTO createAppointmentRequestDTO) {
        var appointment = new Appointment();
        appointment.setDescription(createAppointmentRequestDTO.description());
        appointment.setName(createAppointmentRequestDTO.name());
        appointmentRepository.save(appointment);

        AppointmentNotificationDTO event = new AppointmentNotificationDTO(appointment.getDescription(), appointment.getName());

        appointmentProducerService.sendAppointmentCreated(event);

        return "Appointment id: " + appointment.getId();
    }
}
