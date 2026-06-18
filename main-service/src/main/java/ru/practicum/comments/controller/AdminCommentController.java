package ru.practicum.comments.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.model.UpdateCommentAdminRequest;
import ru.practicum.comments.service.AdminCommentService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    // Вывести все Comments(all)
    @GetMapping("/comments")
    public List<CommentDto> getComments(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        log.info("ADMIN @GetMapping: getComments " +
                        "eventId = {}, userId = {}, status = {}, isNew = {}, " +
                        "rangeStart = {}, rangeEnd = {}, " +
                        "from = {}, size = {}",
                eventId, userId, status, isNew, rangeStart, rangeEnd, from, size);

        return adminCommentService.getComments(eventId, userId, status, isNew, rangeStart, rangeEnd, from, size);
    }

    // Вывести все Comments(all) для Event
    @GetMapping("/events/{eventId}/comments")
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

    @PatchMapping("/comments/{commentId}")
    public CommentDto updateCommentStatusByAdmin(
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentAdminRequest updateCommentAdminRequest) {

        log.info("ADMIN @PatchMapping: updateCommentStatusByAdmin commentId = {}, updateCommentAdminRequest = {}",
                commentId, updateCommentAdminRequest);
        return adminCommentService.updateCommentStatusByAdmin(commentId, updateCommentAdminRequest);
    }

    // Выгрузить комментарий по id для конкретного Event
    @GetMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto getCommentByEventIdAndCommentId(
            @PathVariable Long eventId,
            @PathVariable Long commentId) {

        log.info("ADMIN @GetMapping: getCommentsByEventIdAndCommentId eventId = {}, commentId = {}",
                eventId, commentId);
        return adminCommentService.getCommentByEventIdAndCommentId(eventId, commentId);
    }

    // Удалить комментарии по ids для конкретного Event
    @DeleteMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentsByIds(
            @PathVariable Long eventId,
            @RequestParam List<Long> commentIds) {

        log.info("ADMIN @DeleteMapping: deleteCommentsByIds eventId = {}, commentIds = {}",
                eventId, commentIds);
        adminCommentService.deleteCommentsByIds(eventId, commentIds);
    }

    // Удалить все allComments пользователя User(author) для Event
    @DeleteMapping("/events/{eventId}/comments/by-user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentsByUserId(
            @PathVariable Long eventId,
            @RequestParam Long userId) {
        log.info("ADMIN DeleteMapping: deleteCommentsByUserId eventId = {}, userId = {}",
                eventId, userId);
        adminCommentService.deleteCommentsByUserId(eventId, userId);
    }
}