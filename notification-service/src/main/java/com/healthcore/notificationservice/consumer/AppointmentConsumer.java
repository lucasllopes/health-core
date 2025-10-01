package com.healthcore.notificationservice.consumer;

import com.healthcore.notificationservice.configuration.RabbitMQConstants;
import com.healthcore.notificationservice.dto.NotificationMessageDTO;
import com.healthcore.notificationservice.dto.UpcomingAppointementNotificationDTO;
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
    public void processNotification(NotificationMessageDTO notificationMessageDTO,
                                    Message message,
                                    Channel channel) {
        try {
            logger.info(
                    "Successfully processed appointment notification | " +
                            "appointmentId={}, " +
                            "patientDocument={}, " +
                            "doctorCrm={}, " +
                            "nurseCoren={}, " +
                            "scheduledAt={} ",
                    notificationMessageDTO.appointmentId(),
                    notificationMessageDTO.patient().document(),
                    notificationMessageDTO.doctor().crm(),
                    notificationMessageDTO.nurse().coren(),
                    notificationMessageDTO.createdAt()
            );

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            logger.error("Failed to process appointment notification | messageId={}, deliveryTag={}, error={}",
                    message.getMessageProperties().getMessageId(),
                    message.getMessageProperties().getDeliveryTag(),
                    e.getMessage(),
                    e);
        }
    }

    @RabbitListener(
            queues = RabbitMQConstants.APPOINTMENT_UPCOMING_QUEUE,
            containerFactory = "manualAckListenerContainerFactory"
    )
    public void upcomingAppointment(UpcomingAppointementNotificationDTO upcomingAppointment,
                                    Message message,
                                    Channel channel) {
        try {

            logger.info("""
            [UPCOMING APPOINTMENT NOTIFICATION]
              id: {}
              date: {}
              status: {}
              notes: {}
              patient: name={}, document={}
              doctor : name={}, crm={}
              nurse  : name={}, coren={}
              createdAt: {}
              updatedAt: {}
            """,
                    upcomingAppointment.id(),
                    upcomingAppointment.appointmentDate(),
                    upcomingAppointment.status(),
                    upcomingAppointment.notes(),
                    upcomingAppointment.patient().getName(),
                    upcomingAppointment.patient().getDocument(),
                    upcomingAppointment.doctor().getName(),
                    upcomingAppointment.doctor().getCrm(),
                    upcomingAppointment.nurse().getName(),
                    upcomingAppointment.nurse().getCoren(),
                    upcomingAppointment.createdAt(),
                    upcomingAppointment.updatedAt()
            );
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            logger.error("Failed to log upcoming appointment notification: {}", e.getMessage(), e);
        }
    }
}
