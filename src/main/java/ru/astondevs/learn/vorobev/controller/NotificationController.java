package ru.astondevs.learn.vorobev.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.learn.vorobev.dto.UserEvent;
import ru.astondevs.learn.vorobev.service.EmailService;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendManualNotification(@RequestBody UserEvent request) {
        emailService.sendNotification(request.getEmail(), request.getOperation());
        return ResponseEntity.ok("Уведомление отправлено");
    }
}