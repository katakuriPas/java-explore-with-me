package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation; // Краткое описание

    @NotNull
    private Long category; // id категории

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description; // Полное описание события

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @NotNull
    private Location location; // Широта и долгота места проведения события

    @NotNull
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    private Boolean paid = false; // Нужно ли оплачивать участие

    // Ограничение на количество участников.
    // Значение 0 - означает отсутствие ограничения example: 10 default: 0
    @PositiveOrZero
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    private Long participantLimit = 0L;

    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    private Boolean requestModeration = true; // (example: true, default: true) Нужна ли пре-модерация заявок на участие

    @NotBlank
    @Size(min = 3, max = 120)
    private String title; // Заголовок
}
