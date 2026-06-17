

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.comments.CommentMapperImpl; // Класс, сгенерированный MapStruct
import ru.practicum.comments.CommentStats;
import ru.practicum.comments.CommentStatus;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventMapperImpl;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.Event;
import ru.practicum.stats.StatsManager;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Быстрая инициализация моков без поднятия Spring
class EventMapperTest {

    // Создаем экземпляр реализации маппера, сгенерированного MapStruct
    private final EventMapper eventMapper = new EventMapperImpl();

    @Mock
    private StatsManager statsManager;

    @BeforeEach
    void setUp() {
        // Внедряем заглушку StatsManager в абстрактный класс EventMapper
        ReflectionTestUtils.setField(eventMapper, "statsManager", statsManager);

        // Внедряем сгенерированный CommentMapperImpl, который используется через uses
        ReflectionTestUtils.setField(eventMapper, "commentMapper", new CommentMapperImpl());
    }

    @Test
    @DisplayName("Должен корректно маппить Event в EventFullDto вместе со списком комментариев")
    void shouldMapEventToEventFullDtoWithComments() {
        // Given
        User author = User.builder()
                .id(2L)
                .name("Иван Иванов")
                .email("ivan@test.ru")
                .build();

        Event event = Event.builder()
                .id(1L)
                .title("Тестовое событие")
                .annotation("Краткое описание")
                .description("Полное описание")
                .comments(new ArrayList<>())
                .build();

        Comment comment = Comment.builder()
                .id(10L)
                .description("Отличный комментарий!")
                .event(event)
                .author(author)
                .createdOn(LocalDateTime.now())
                .status(CommentStatus.NEUTRAL)
                .build();

        event.getComments().add(comment);

        // When
        EventFullDto resultDto = eventMapper.toEventFullDto(event);

        // Then
        assertNotNull(resultDto, "EventFullDto не должен быть null");
        assertEquals(event.getId(), resultDto.getId());
        assertEquals(event.getTitle(), resultDto.getTitle());

        // Проверяем маппинг вложенных комментариев
        assertNotNull(resultDto.getComments(), "Список комментариев не должен быть null");
        assertEquals(1, resultDto.getComments().size());

        CommentShortDto commentShortDto = resultDto.getComments().get(0);
        assertEquals(comment.getId(), commentShortDto.getId());
        assertEquals(comment.getDescription(), commentShortDto.getDescription());
        assertEquals(author.getId(), commentShortDto.getAuthorId(), "ID автора должен извлекаться автоматически");
    }
}
