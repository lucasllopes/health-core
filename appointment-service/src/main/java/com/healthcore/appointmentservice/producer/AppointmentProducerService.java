package com.healthcore.appointmentservice.producer;

import com.healthcore.appointmentservice.configuration.RabbitMQConstants;
import com.healthcore.appointmentservice.dto.message.AppointmentNotificationDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class AppointmentProducerService {

    private final RabbitTemplate rabbitTemplate;

    public AppointmentProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAppointmentCreated(AppointmentNotificationDTO event) {
        if (event == null) {
            throw new IllegalArgumentException("AppointmentNotificationDTO não pode ser nulo.");
        }

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.EXCHANGE_NAME,
                    RabbitMQConstants.ROUTING_KEY_NEW,
                    event
            );
            System.out.println("Appointment created event sent: " + event);
        } catch (Exception e) {
            System.err.println("Failed to send appointment created event: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar notificação de agendamento.", e);
        }
    }
}

