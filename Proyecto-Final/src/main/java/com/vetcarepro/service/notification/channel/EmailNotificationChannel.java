package com.vetcarepro.service.notification.channel;

import org.springframework.stereotype.Component;

import com.vetcarepro.domain.enums.NotificationChannelType;
import com.vetcarepro.service.notification.facade.EmailClientFacade;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {

    private final EmailClientFacade emailClientFacade;

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.EMAIL;
    }

    @Override
    public void send(String recipient, String message) {
        emailClientFacade.sendEmail(recipient, "Vet Care Pro reminder", message);
    }
}
