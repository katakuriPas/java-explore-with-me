package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enumState.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.stats.StatsManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicEventService {
    private static final String EVENT_NOT_FOUND = "Event with id=%d was not found";

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatsManager statsManager;

    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND.formatted(eventId)));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("The event must be published");
        }

        statsManager.sendHit(request);

        return eventMapper.toEventFullDto(event);
    }

    public List<EventShortDto> getEventsWithFilter(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            String sort,
            int from, int size, HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }

        Pageable pageable = PageRequest.of(from / size, size);
        if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        }

        Page<Event> eventsPage = eventRepository.getEventsWithFilter(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);

        List<Event> events = eventsPage.getContent();
        if (events.isEmpty()) {
            return List.of();
        }

        statsManager.sendHit(request);

        List<EventShortDto> eventShortDtos = eventMapper.toEventShortDtoList(events);

        if ("VIEWS".equalsIgnoreCase(sort)) {
            List<EventShortDto> sortedDtos = new ArrayList<>(eventShortDtos);
            sortedDtos.sort((d1, d2) -> Long.compare(d2.getViews(), d1.getViews()));
            return sortedDtos;
        }

        return eventShortDtos;
    }
}
