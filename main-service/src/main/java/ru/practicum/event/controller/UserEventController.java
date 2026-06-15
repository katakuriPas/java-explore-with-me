package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.service.UserEventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.EventRequestStatusUpdateResult;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class UserEventController {

    private final UserEventService userEventService;

    // --- POST ------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto newEventDto,
                                    @PathVariable Long userId) {
        log.info("PostMapping: createEvent (newEventDto: {}, userId: {})", newEventDto, userId);

        return userEventService.createEvent(newEventDto, userId);
    }

    // --- GET ------------------------------------------------
    @GetMapping
    public List<EventShortDto> getEvetByUserFromAndSize(
            @PathVariable Long userId,
            @RequestParam(name = "from", defaultValue = "0") Long from,
            @RequestParam(name = "size", defaultValue = "10") Long size,
            HttpServletRequest request
    ) {
        log.info("GetMapping: getEvetByUserFromAndSize userId = {}, from = {}, size = {}, request = {})", userId, from, size, request);
        return userEventService.getEvetByUserFromAndSize(userId, from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvetByUser(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            HttpServletRequest request
    ) {
        log.info("GetMapping: getEvetByUser userId = {}, eventId = {}, request = {})", userId, eventId, request);
        return userEventService.getEvetByUser(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestByEventIdAndEventId(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("GetMapping: getRequestByUserIdAndEventId userId = {}, eventId = {})", userId, eventId);

        return userEventService.getRequestByUserIdAndEventId(userId, eventId);
    }

    // --- PATCH ------------------------------------------------
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEvent) {
        log.info("@PatchMapping: " +
                "updateEvent userId = {}, " +
                "eventId = {}, " +
                "UpdateEventUserRequest = {})", userId, eventId, updateEvent);
        return userEventService.updateEvent(userId, eventId, updateEvent);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody(required = false) EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("@PatchMapping: " +
                "updateEventRequestStatus" +
                "userId = {}, " +
                "eventId = {}, " +
                "EventRequestStatusUpdateRequest = {})", userId, eventId, eventRequestStatusUpdateRequest);

        return userEventService.updateEventRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }


}
