package com.healthcore.notificationservice.configuration;

public class RabbitMQConstants {

    public static final String EXCHANGE_NAME = "appointment_exchange";

    public static final String NOTIFICATION_QUEUE= "notification_queue";
    public static final String APPOINTMENT_UPCOMING_QUEUE = "appointment_upcoming_queue";

    public static final String ROUTING_KEY_NEW = "appointment.new";
    public static final String ROUTING_KEY_UPCOMING = "appointment.upcoming";

}
