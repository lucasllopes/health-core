package com.healthcore.notificationservice.consumer;

import com.healthcore.notificationservice.configuration.RabbitMQConstants;
import com.healthcore.notificationservice.dto.NotificationMessageDTO;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentConsumer {

    private final Logger logger = LoggerFactory.getLogger(AppointmentConsumer.class);

    @RabbitListener(
            queues = RabbitMQConstants.NOTIFICATION_QUEUE,
            containerFactory = "manualAckListenerContainerFactory"
    )
    public void processCancellation(NotificationMessageDTO cancellation,
                                    Message message,
                                    Channel channel) {
        try {
            logger.info("notification : {}", cancellation);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            logger.error("Error processing cancellation for reservation: {}", cancellation);
        }
    }

 //   @RabbitListener(queues = RabbitMQConstants.NOTIFICATION_QUEUE)
 //   public void processNotificationMessage(NotificationMessageDTO notificationMessageDTO) {
 //       logger.info("Received notification message: {}", notificationMessageDTO);
   // }
}
