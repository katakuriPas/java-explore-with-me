package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.enumState.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {
    private static final String EVENT_NOT_FOUND = "Event with id=%d was not found";
    private static final String USER_NOT_FOUND = "User with id=%d was not found";
    private static final String REQUEST_NOT_FOUND = "Request with id=%d was not found";


    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND.formatted(eventId)));

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userId)));


        // нельзя добавить повторный запрос (Ожидается код ошибки 409)
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.error("RequestService: createRequest(Long userId = {}, Long eventId = {}) " +
                    "- You cannot add a duplicate request.", userId, eventId);

            throw new DataIntegrityViolationException("You cannot add a duplicate request.");
        }

        // инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (Objects.equals(existingEvent.getInitiator().getId(), userId)) {
            log.error("RequestService: createRequest(Long userId = {}, Long eventId = {}) " +
                    "- The event initiator cannot add a request to participate in their event.", userId, eventId);

            throw new DataIntegrityViolationException("The event initiator cannot add a request to participate in their event.");
        }

        // нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (existingEvent.getState() != EventState.PUBLISHED) {
            log.error("RequestService: createRequest(Long userId = {}, Long eventId = {}) " +
                    "- You cannot participate in an unpublished event.", userId, eventId);

            throw new DataIntegrityViolationException("You cannot participate in an unpublished event.");
        }

        // если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        if (existingEvent.getParticipantLimit() > 0) {
            long confirmedRequests = existingEvent.getConfirmedRequests();
            if (confirmedRequests >= existingEvent.getParticipantLimit()) {
                log.error("RequestService: createRequest(Long userId = {}, Long eventId = {}) " +
                        "- The participation request limit has been reached", userId, eventId);

                throw new DataIntegrityViolationException("The participation request limit has been reached");
            }
        }

        Request newRequest = new Request();
        newRequest.setRequester(existingUser);
        newRequest.setEvent(existingEvent);
        newRequest.setCreated(LocalDateTime.now());

        // если для события отключена пре-модерация запросов на участие,
        // то запрос должен автоматически перейти в состояние подтвержденного
        if (!existingEvent.getRequestModeration() || existingEvent.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }

        Request savedRequest = requestRepository.save(newRequest);

        if (savedRequest.getStatus() == RequestStatus.CONFIRMED) {
            existingEvent.setConfirmedRequests(existingEvent.getConfirmedRequests() + 1);
            eventRepository.save(existingEvent);
        }

        return requestMapper.toRequestDto(savedRequest);
    }

    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userId)));

        List<Request> requests = requestRepository.findAllRequestsByUserId(userId);

        return requests.stream().map(requestMapper::toRequestDto).toList();
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(userId)));

        requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(REQUEST_NOT_FOUND.formatted(requestId)));

        Request existingRequest = requestRepository.findByUserIdAndRequestId(userId, requestId);

        existingRequest.setStatus(RequestStatus.CANCELED);

        return requestMapper.toRequestDto(requestRepository.save(existingRequest));
    }
}
