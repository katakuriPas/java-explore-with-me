package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.event.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {

    private final AdminEventService adminEventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable Long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest updateEvent) {
        log.info("PatchMapping: updateEventAdmin eventId = {}, UpdateEventAdminRequest = {}",
                eventId,
                updateEvent);

        return adminEventService.updateEventAdmin(eventId, updateEvent);
    }

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {


        log.info("GetMapping: getEventsByAdmin " +
                        "users = {}, states = {}, categories = {}," +
                        "rangeStart = {}, rangeEnd = {}, " +
                        "from = {}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return adminEventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/all")
    public List<EventFullDto> getAllEvents() {
        return adminEventService.getAllEvents();
    }


}
