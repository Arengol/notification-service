package ru.astondevs.learn.vorobev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель события пользователя")
public class UserEvent {

    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Тип операции", example = "CREATE", allowableValues = {"CREATE", "DELETE"})
    private String operation; // "CREATE" или "DELETE"
}
