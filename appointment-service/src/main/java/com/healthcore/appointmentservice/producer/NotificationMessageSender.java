package com.healthcore.appointmentservice.producer;

import com.healthcore.appointmentservice.configuration.RabbitMQConstants;
import com.healthcore.appointmentservice.dto.AppointmentNotificationMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageSender {
    private static final Logger log = LoggerFactory.getLogger(NotificationMessageSender.class);

    private final RabbitTemplate rabbitTemplate;

    public NotificationMessageSender(RabbitTemplate rabbitTemplate
    ) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(AppointmentNotificationMessageDTO message) {
        log.info("Enviando mensagem para a fila de notificação: {}", RabbitMQConstants.NOTIFICATION_QUEUE);
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME, RabbitMQConstants.ROUTING_KEY_NEW, message);
        log.info("Mensagem enviada para a fila {} com sucesso!", RabbitMQConstants.NOTIFICATION_QUEUE);
    }
}