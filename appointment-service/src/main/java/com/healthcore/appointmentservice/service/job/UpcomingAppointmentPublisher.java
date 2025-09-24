package com.healthcore.appointmentservice.service.job;

import com.healthcore.appointmentservice.configuration.RabbitMQConstants;
import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class UpcomingAppointmentPublisher {

    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;

    public UpcomingAppointmentPublisher(AppointmentRepository repo, RabbitTemplate rabbit) {
        this.appointmentRepository = repo;
        this.rabbitTemplate = rabbit;
    }

    @Scheduled(cron = "0 */2 * * * *", zone = "America/Sao_Paulo")
    public void publishTomorrowAppointments() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

        LocalDateTime end = now.plusDays(1);

        var appointments = appointmentRepository.findByAppointmentDateBetweenAndStatus(now, end, "AGENDADO");

        for (Appointment appointment : appointments) {
            var msg = appointment.getId() + "-" + appointment.getPatient().getEmail();

            rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME, RabbitMQConstants.ROUTING_KEY_UPCOMING, msg);
        }
    }

}
