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
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE_NAME,
                RabbitMQConstants.ROUTING_KEY_NEW,
                event
        );
    }
}

