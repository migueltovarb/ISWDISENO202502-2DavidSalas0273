package com.vetcarepro.service.notification;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.vetcarepro.domain.enums.NotificationChannelType;
import com.vetcarepro.service.notification.channel.NotificationChannel;

import jakarta.annotation.PostConstruct;

@Component
public class NotificationChannelFactory {

    private final Map<NotificationChannelType, NotificationChannel> channels = new EnumMap<>(NotificationChannelType.class);
    private final List<NotificationChannel> implementations;

    public NotificationChannelFactory(List<NotificationChannel> implementations) {
        this.implementations = implementations;
    }

    @PostConstruct
    void init() {
        implementations.forEach(channel -> channels.put(channel.getType(), channel));
    }

    public NotificationChannel get(NotificationChannelType type) {
        NotificationChannel channel = channels.get(type);
        if (channel == null) {
            throw new IllegalArgumentException("Channel not implemented: " + type);
        }
        return channel;
    }
}
