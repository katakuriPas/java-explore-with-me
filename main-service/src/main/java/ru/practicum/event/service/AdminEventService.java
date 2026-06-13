package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryService;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.enumState.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventService {
    private static final String EVENT_NOT_FOUND = "Event with id=%d was not found";

    private final CategoryService categoryService;

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    private final EventMapper eventMapper;

    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND.formatted(eventId)));

        //  Событие можно публиковать, только если оно в состоянии ожидания публикации
        //  Событие можно отклонить, только если оно еще не опубликовано
        //  (Ожидается код ошибки 409)
        if (updateEvent.getStateAction() != null) {
            String action = updateEvent.getStateAction();

            if ("PUBLISH_EVENT".equals(action)) {
                if (existingEvent.getState() != EventState.PENDING) {
                    throw new DataIntegrityViolationException(
                            "An event can only be published if it is in the pending publishing state");
                }
                existingEvent.setState(EventState.PUBLISHED);
                existingEvent.setPublishedOn(LocalDateTime.now());
            } else if ("REJECT_EVENT".equals(action)) {
                if (existingEvent.getState() == EventState.PUBLISHED) {
                    throw new DataIntegrityViolationException(
                            "An event can only be declined if it has not yet been published");
                }
                existingEvent.setState(EventState.CANCELED);
            }
        }

        eventMapper.updateEventAdmin(updateEvent, existingEvent);

        //  Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.
        //  (Ожидается код ошибки 409)
        if (updateEvent.getEventDate() != null
                && updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException(
                    "The start date of the event being modified " +
                            "must be no earlier than one hour from the publication date");
        }

        Long categoryUpdateId = updateEvent.getCategory();
        if (categoryUpdateId != null) {
            existingEvent.setCategory(categoryService.getCategoryById(categoryUpdateId));
        }

        Location newLocation = updateEvent.getLocation();
        if (newLocation != null) {
            existingEvent.setLocation(locationRepository.save(newLocation));
        }

        return eventMapper.toEventFullDto(eventRepository.save(existingEvent));
    }

    public List<EventFullDto> getEventsByAdmin(
            List<Long> users, List<String> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd,
            int from, int size) {

        //  Запрос составлен некорректно
        //  (Ожидается код ошибки 400)
        if (users != null && !users.isEmpty()) {
            List<User> userEntityList = userRepository.findAllById(users);
            if (userEntityList.size() != users.size()) {
                throw new BadRequestException("Not all users are present in the database");
            }
        }

        //  Запрос составлен некорректно
        //  (Ожидается код ошибки 400)
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("The range start date cannot be later than the end date");
        }

        List<EventState> eventStates = null;
        if (states != null) {
            eventStates = states.stream()
                    .map(EventState::valueOf)
                    .toList();
        }

        List<Long> finalUsers = (users == null || users.isEmpty()) ? null : users;
        List<EventState> finalStates = (eventStates == null || eventStates.isEmpty()) ? null : eventStates;
        List<Long> finalCategories = (categories == null || categories.isEmpty()) ? null : categories;

        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.getEventsByAdmin(
                finalUsers, finalStates, finalCategories, rangeStart, rangeEnd, pageable).getContent();

        return eventMapper.toEventFullDtoList(events);
    }

    public List<EventFullDto> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream().map(eventMapper::toEventFullDto).toList();
    }
}
