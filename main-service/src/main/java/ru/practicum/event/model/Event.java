package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.Category;
import ru.practicum.event.enumState.EventState;
import ru.practicum.location.Location;
import ru.practicum.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000, nullable = false)
    private String annotation; // Краткое описание

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category; // Категория

    @Column(name = "confirmed_requests")
    private Long confirmedRequests; // Количество одобренных заявок на участие в данном событии

    @Column(name = "created_on")
    @Builder.Default
    private LocalDateTime createdOn = LocalDateTime.now(); // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(length = 7000, nullable = false)
    private String description; // Полное описание события

    @Column(name = "event_date")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "initiator_id")
    private User initiator; // Пользователь (краткая информация)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location; // Широта и долгота места проведения события

    @Column(nullable = false)
    private Boolean paid; // Нужно ли оплачивать участие

    // Ограничение на количество участников.
    // Значение 0 - означает отсутствие ограничения example: 10 default: 0
    @Column(name = "participant_limit", nullable = false)
    private Long participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration; // (example: true, default: true) Нужна ли пре-модерация заявок на участие

    // (example: PUBLISHED) Список состояний жизненного цикла события
    // Enum:[ PENDING, PUBLISHED, CANCELED ]
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    @Builder.Default
    private EventState state = EventState.PENDING;

    @Column(name = "title", length = 150, nullable = false)
    private String title; // Заголовок
}
