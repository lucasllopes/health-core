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
                            "scheduledAt={}, " +
                            "messageId={}, ",
                    notificationMessageDTO.id(),
                    notificationMessageDTO.patientId(),
                    notificationMessageDTO.doctorId(),
                    notificationMessageDTO.createdAt(),
                    message.getMessageProperties().getMessageId()
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

            String patientName = null;
            String patientDoc  = null;
            if (upcomingAppointment.patient() != null) {
                patientName = upcomingAppointment.patient().getName();
                patientDoc  = upcomingAppointment.patient().getDocument();
            }

            String doctorName = null;
            String doctorCrm  = null;
            if (upcomingAppointment.doctor() != null) {
                doctorName = upcomingAppointment.doctor().getName();
                doctorCrm  = upcomingAppointment.doctor().getCrm();
            }

            String nurseName = null;
            String nurseCoren = null;
            if (upcomingAppointment.nurse() != null) {
                nurseName  = upcomingAppointment.nurse().getName();
                nurseCoren = upcomingAppointment.nurse().getCoren();
            }

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
                    patientName, patientDoc,
                    doctorName, doctorCrm,
                    nurseName, nurseCoren,
                    upcomingAppointment.createdAt(),
                    upcomingAppointment.updatedAt()
            );
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            logger.error("Failed to log upcoming appointment notification: {}", e.getMessage(), e);
        }
    }
}
