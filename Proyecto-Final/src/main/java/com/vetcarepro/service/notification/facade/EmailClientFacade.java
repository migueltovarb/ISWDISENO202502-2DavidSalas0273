package com.vetcarepro.service.notification.facade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailClientFacade {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@vetcarepro.local}")
    private String sender;

    public void sendEmail(String recipient, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.debug("Email dispatched to {}", recipient);
        } catch (Exception ex) {
            log.warn("Unable to send email. Fallback logging message to {} - {}", recipient, body);
        }
    }
}
