package ru.practicum.comments.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.UserCommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class UserCommentController {

    private final UserCommentService userCommentService;

    // Создание Comment для Event
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid NewCommentDto newComment) {

        log.info("USER @PostMapping: createComment userId = {}, eventId = {}, newComment = {}",
                userId, eventId, newComment);
        return userCommentService.createComment(userId, eventId, newComment);
    }

    // Получить все комментарии User(author) для конкретного Event
    @GetMapping
    public List<CommentDto> getCommentsByAuthorIdAndEventIdAnd(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.info("USER @GetMapping: getCommentsByEventIdAndAuthorId userId = {}, eventId = {}",
                userId, eventId);
        return userCommentService.getCommentsByEventIdAndAuthor(userId, eventId);
    }

    // Редактирование Comment
    @PatchMapping("/{commentId}")
    public CommentDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestBody @Valid NewCommentDto newComment) {

        log.info("USER @PatchMapping: updateComment " +
                        "userId = {}, eventId = {}, commentId = {}, newComment = {}",
                userId, eventId, commentId, newComment);
        return userCommentService.updateComment(userId, eventId, commentId, newComment);
    }

    // Удалить комментарий
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId) {

        log.info("USER @DeleteMapping: deleteComment userId = {}, eventId = {}, commentId = {}",
                userId, eventId, commentId);
        userCommentService.deleteComment(userId, eventId, commentId);
    }
}
