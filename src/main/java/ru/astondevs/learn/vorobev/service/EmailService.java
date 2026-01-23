package ru.astondevs.learn.vorobev.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendNotification(String email, String operation) {
        String subject = "Уведомление от сайта";
        String text;

        if ("DELETE".equalsIgnoreCase(operation)) {
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else if ("CREATE".equalsIgnoreCase(operation)) {
            text = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
        } else {
            log.warn("Неизвестная операция: {}", operation);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@astondevs.ru");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
        log.info("Email отправлен на {} с операцией {}", email, operation);
    }
}
