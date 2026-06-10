package ru.practicum.stats;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsManager {

    private final StatsClient statsClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void sendHit(HttpServletRequest request) {
        try {
            statsClient.hit(EndpointHitDto.builder()
                    .app("ewm-main-service")
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.error("Ошибка сохранения статистики для URI {}: {}", request.getRequestURI(), e.getMessage());
        }
    }

    //  Получение списка ViewStatsDto для одного конкретного события
    public List<ViewStatsDto> getStatsListForSingleEvent(String uri, LocalDateTime publishedOn) {
        if (publishedOn == null) {
            return List.of();
        }
        try {
            ResponseEntity<Object> response = statsClient.getStats(
                    publishedOn.format(formatter),
                    LocalDateTime.now().format(formatter),
                    List.of(uri),
                    false
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return objectMapper.convertValue(
                        response.getBody(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ViewStatsDto.class)
                );
            }
        } catch (Exception e) {
            log.error("Ошибка получения одиночной статистики по URI {}: {}", uri, e.getMessage());
        }
        return List.of();
    }

    public void enrichWithViews(List<? extends Object> dtos, List<Event> events) {
        if (events == null || events.isEmpty() || dtos == null || dtos.isEmpty()) {
            return;
        }

        LocalDateTime minPublished = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusDays(1));

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());

        try {
            ResponseEntity<Object> response = statsClient.getStats(
                    minPublished.format(formatter),
                    LocalDateTime.now().format(formatter),
                    uris,
                    false
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> stats = objectMapper.convertValue(response.getBody(), List.class);

                Map<String, Long> viewsMap = stats.stream().collect(Collectors.toMap(
                        stat -> (String) stat.get("uri"),
                        stat -> ((Number) stat.get("hits")).longValue(),
                        (existing, replacement) -> existing
                ));

                for (Object dto : dtos) {
                    if (dto instanceof EventShortDto) {
                        EventShortDto shortDto = (EventShortDto) dto;
                        shortDto.setViews(viewsMap.getOrDefault("/events/" + shortDto.getId(), 0L));
                    } else if (dto instanceof EventFullDto) {
                        EventFullDto fullDto = (EventFullDto) dto;
                        fullDto.setViews(viewsMap.getOrDefault("/events/" + fullDto.getId(), 0L));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при пакетном получении статистики: {}", e.getMessage());
        }
    }
}
