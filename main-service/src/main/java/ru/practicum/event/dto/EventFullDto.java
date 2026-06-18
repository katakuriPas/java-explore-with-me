package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.event.enumState.EventState;
import ru.practicum.location.LocationDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {

    private Long id;

    private String annotation; // Краткое описание

    private CategoryDto category; // Категория

    private Long confirmedRequests; // Количество одобренных заявок на участие в данном событии

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn; // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    private String description; // Полное описание события

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private UserShortDto initiator; // Пользователь (краткая информация)

    private LocationDto location; // Широта и долгота места проведения события

    private Boolean paid; // Нужно ли оплачивать участие

    // Ограничение на количество участников.
    // Значение 0 - означает отсутствие ограничения example: 10 default: 0
    private Long participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    private Boolean requestModeration; // (example: true, default: true) Нужна ли пре-модерация заявок на участие

    // (example: PUBLISHED) Список состояний жизненного цикла события
    // Enum:[ PENDING, PUBLISHED, CANCELED ]
    private EventState state;

    private String title; // Заголовок

    private Long views; // Количество просмотрев события

    private List<CommentShortDto> comments;
}
