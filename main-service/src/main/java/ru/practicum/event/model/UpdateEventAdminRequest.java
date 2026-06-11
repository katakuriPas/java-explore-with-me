package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.location.Location;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation; // Краткое описание

    private Long category; // id категории

    @Size(min = 20, max = 7000)
    private String description; // Полное описание события

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private Location location; // Широта и долгота места проведения события

    private Boolean paid; // Нужно ли оплачивать участие

    // Ограничение на количество участников.
    // Значение 0 - означает отсутствие ограничения example: 10 default: 0
    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration; // (example: true, default: true) Нужна ли пре-модерация заявок на участие

    private String stateAction; // (ENUM: SEND_TO_REVIEW, CANCEL_REVIEW) Изменение состояния события

    @Size(min = 3, max = 120)
    private String title; // Заголовок
}
