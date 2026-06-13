package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryService;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.enumState.EventState;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationRepository;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.RequestStatus;
import ru.practicum.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.EventRequestStatusUpdateResult;
import ru.practicum.request.model.Request;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestRepository;
import ru.practicum.stats.StatsManager;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventService {
    private static final String EVENT_NOT_FOUND = "Event with id=%d was not found";

    private final StatsManager statsManager;

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;

    private final UserService userService;
    private final CategoryService categoryService;

    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        if (newEventDto.getEventDate() != null
                && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                    "The date and time of the scheduled event cannot be earlier than two hours from the current moment");
        }

        User initiator = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(newEventDto.getCategory());
        Location location = locationRepository.save(newEventDto.getLocation());

        Event savedEvent = eventRepository.save(eventMapper.toEvent(newEventDto, category, initiator, location));

        log.info("EventService: Сохранение МЕРОПРИЯТИЯ (Event) = {}", savedEvent);
        return eventMapper.toEventFullDto(savedEvent);
    }

    public List<EventShortDto> getEvetByUserFromAndSize(Long userId, Long from, Long size, HttpServletRequest request) {
        userService.getUserById(userId);

        List<Event> events = eventRepository.getEvetByUserFromAndSize(userId, from, size);

        //statsManager.sendHit(request);

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    public EventFullDto getEvetByUser(Long userId, Long eventId, HttpServletRequest request) {
        userService.getUserById(userId);

        Event event = eventRepository.getEvetByUser(userId, eventId);

        //statsManager.sendHit(request);

        return eventMapper.toEventFullDto(event);
    }

    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND.formatted(eventId)));

        if (!existingEvent.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("The user with id=" + userId + " is not the initiator of this event");
        }

        if (existingEvent.getState() == EventState.PUBLISHED) {
            throw new DataIntegrityViolationException("The item cannot be edited because it is already published");
        }

        if (updateEvent.getEventDate() != null
                && updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                    "The date and time of the scheduled event cannot be earlier than two hours from the current moment");
        }

        eventMapper.updateEvent(updateEvent, existingEvent);

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

    public List<ParticipationRequestDto> getRequestByUserIdAndEventId(Long userId, Long eventId) {
        userService.getUserById(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND.formatted(eventId)));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("User is not the initiator of this event");
        }

        List<Request> request = requestRepository.findAllByEventId(eventId);
        log.info("findAllByEventId eventId = {}: {}", eventId, request);
       // log.info("getRequestByUserIdAndEventId userId = {}, eventId = {}: {}", userId, eventId, request);

        return request.stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }


    public EventRequestStatusUpdateResult updateEventRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        if (eventRequestStatusUpdateRequest == null) {
            throw new DataIntegrityViolationException("eventRequestStatusUpdateRequest: " +
                    "The request body cannot be empty  ");
        }

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND.formatted(eventId)));

        User existingUser = userService.getUserById(userId);

        //  менять может только автор мероприятия
        if (existingEvent.getInitiator() != existingUser) {
            throw new DataIntegrityViolationException("You are not the initiator of this event");
        }

        //  если для события лимит заявок равен 0 или отключена пре-модерация заявок,
        //  то подтверждение заявок не требуется
        if (existingEvent.getParticipantLimit() == 0 || !existingEvent.getRequestModeration()) {
            throw new DataIntegrityViolationException("This event does not require confirmation");
        }

        //  нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if (existingEvent.getParticipantLimit() <= existingEvent.getConfirmedRequests()) {
            log.info("existingEvent = {}, existingEvent.getParticipantLimit() = {}, existingEvent.getConfirmedRequests() = {}",
                    existingEvent, existingEvent.getParticipantLimit(), existingEvent.getConfirmedRequests());
            throw new DataIntegrityViolationException("The participant limit has been reached");
        }

        List<Request> requestsList = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());

        if (requestsList.size() != eventRequestStatusUpdateRequest.getRequestIds().size()) {
            throw new NotFoundException("Some participation requests were not found");
        }
        //  статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        for (Request request : requestsList) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException(
                        "The status can only be changed for applications that are in a <<PENDING>> state");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>(); // подтвержденные заявки
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();  // отмененные заявки

        String targetStatus = eventRequestStatusUpdateRequest.getStatus();

        for (Request request : requestsList) {
            if ("REJECTED".equals(targetStatus)) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                rejectedRequests.add(requestMapper.toRequestDto(request));

            } else if ("CONFIRMED".equals(targetStatus)
                    && existingEvent.getParticipantLimit() > existingEvent.getConfirmedRequests()) {
                request.setStatus(RequestStatus.CONFIRMED);
                requestRepository.save(request);
                existingEvent.setConfirmedRequests(existingEvent.getConfirmedRequests() + 1);
                confirmedRequests.add(requestMapper.toRequestDto(request));

            } else {
                // если при подтверждении данной заявки, лимит заявок для события исчерпан,
                // то все неподтверждённые заявки необходимо отклонить
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                rejectedRequests.add(requestMapper.toRequestDto(request));
            }
        }

        eventRepository.save(existingEvent);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }
}
