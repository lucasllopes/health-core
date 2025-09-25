package com.healthcore.appointmentservice.producer;

import com.healthcore.appointmentservice.dto.AppointmentNotificationMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageSender {
    private static final Logger log = LoggerFactory.getLogger(NotificationMessageSender.class);

    private final RabbitTemplate rabbitTemplate;
    private final String notificationQueue;

    public NotificationMessageSender(RabbitTemplate rabbitTemplate,
                                     @Value("${rabbitmq.notification-queue:NOTIFICATION_QUEUE}") String notificationQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationQueue = notificationQueue;
    }

    public void sendNotification(AppointmentNotificationMessageDTO message) {
        log.info("Enviando mensagem para a fila de notificação: {}", notificationQueue);
        rabbitTemplate.convertAndSend(notificationQueue, message);
        log.info("Mensagem enviada para a fila {} com sucesso!", notificationQueue);
    }
}

