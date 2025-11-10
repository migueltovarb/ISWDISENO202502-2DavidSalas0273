package com.vetcarepro.service.notification.channel;

import com.vetcarepro.domain.enums.NotificationChannelType;

public interface NotificationChannel {

    NotificationChannelType getType();

    void send(String recipient, String message);
}
