package com.vetcarepro.service.notification.channel;

import org.springframework.stereotype.Component;

import com.vetcarepro.domain.enums.NotificationChannelType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WhatsappNotificationChannel implements NotificationChannel {

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.WHATSAPP;
    }

    @Override
    public void send(String recipient, String message) {
        log.info("WhatsApp to {} -> {}", recipient, message);
    }
}
