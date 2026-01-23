package ru.astondevs.learn.vorobev.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.astondevs.learn.vorobev.dto.UserEvent;
import ru.astondevs.learn.vorobev.service.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaNotificationListener {

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        log.info("Получено сообщение из Kafka: {}", event);
        emailService.sendNotification(event.getEmail(), event.getOperation());
    }
}
