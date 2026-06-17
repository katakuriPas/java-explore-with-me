package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.AdminCommentService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {

    private final AdminCommentService adminCommentService;

    @GetMapping
    public List<CommentDto> getCommentsByEventId(
            @PathVariable Long eventId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        log.info("ADMIN @GetMapping: getCommentsByEventId " +
                        "eventId = {}, userId = {}, status = {}, isNew = {}, " +
                        "rangeStart = {}, rangeEnd = {}, " +
                        "from = {}, size = {}",
                eventId, userId, status, isNew, rangeStart, rangeEnd, from, size);

        return adminCommentService.getComments(eventId, userId, status, isNew, rangeStart, rangeEnd, from, size);
    }
}
