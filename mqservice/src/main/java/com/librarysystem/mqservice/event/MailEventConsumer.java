package com.librarysystem.mqservice.event;

import com.librarysystem.commonservice.services.mailsender.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MailEventConsumer {

    private final EmailService emailService;

    public MailEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "emailBasic", containerFactory = "kafkaListenerContainerFactory")
    public void testEmail(String toEmail) {
        log.info("Received message from testEmail: " + toEmail);

        String template = "<div>\n" +
                "    <h1>Welcome, %s!</h1>\n" +
                "    <p>Thank you for joining us. We're excited to have you on board.</p>\n" +
                "    <p>Your username is: <strong>%s</strong></p>\n" +
                "</div>";
        String filledTemplate = String.format(template, "Con Vit", toEmail);

        emailService.sendEmail(toEmail, "Thank for booking", filledTemplate, true, null);
    }

    @KafkaListener(topics = "emailTemplate", containerFactory = "kafkaListenerContainerFactory")
    public void emailTemplate(String toEmail) {
        log.info("Received message from emailTemplate: " + toEmail);

        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("name", "Con vit");

        emailService.sendEmailWithTemplate(toEmail, "Welcome to Christmas", "emailTemplate.ftl", placeholders, null);
    }
}
