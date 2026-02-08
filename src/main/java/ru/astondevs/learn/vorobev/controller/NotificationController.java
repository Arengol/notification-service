package ru.astondevs.learn.vorobev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.learn.vorobev.dto.NotificationResponse;
import ru.astondevs.learn.vorobev.dto.UserEvent;
import ru.astondevs.learn.vorobev.service.EmailService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Контроллер для ручной отправки уведомлений")
public class NotificationController {

    private final EmailService emailService;

    @Operation(
            summary = "Отправить ручное уведомление",
            description = "Принимает данные события пользователя и отправляет email.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Уведомление успешно отправлено",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.class))
                    )
            }
    )
    @PostMapping("/send")
    public ResponseEntity<EntityModel<NotificationResponse>> sendManualNotification(@RequestBody UserEvent request) {
        emailService.sendNotification(request.getEmail(), request.getOperation());
        NotificationResponse responseData = new NotificationResponse("Уведомление отправлено");
        EntityModel<NotificationResponse> resource = EntityModel.of(responseData);
        Link selfLink = linkTo(methodOn(NotificationController.class)
                .sendManualNotification(request))
                .withSelfRel();
        resource.add(selfLink);
        return ResponseEntity.ok(resource);
    }
}