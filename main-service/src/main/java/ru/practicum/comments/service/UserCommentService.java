package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.CommentMapper;
import ru.practicum.comments.CommentRepository;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.UserEventService;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommentService {
    private static final String COMMENT_NOT_FOUND = "Comment with id=%d was not found";

    private final UserService userService;
    private final UserEventService userEventService;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newComment) {
        User existingAuthor = userService.getUserById(userId);
        Event existingEvent = userEventService.getEventEntityById(eventId);

        Comment comment = commentMapper.toCommentByNew(newComment);

        comment.setAuthor(existingAuthor);
        comment.setEvent(existingEvent);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created = {}", savedComment);

        return commentMapper.toCommentDto(savedComment);
    }

    public List<CommentDto> getCommentsByEventIdAndAuthor(Long userId, Long eventId) {
        userService.getUserById(userId);
        userEventService.getEventEntityById(eventId);

        List<Comment> comments = commentRepository.findAllByAuthorIdAndEventId(userId, eventId);

        log.info("Comments received (userId = {}, eventId = {}): {}",
                userId, eventId, comments);
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto updateComment) {
        User existingUser = userService.getUserById(userId);
        userEventService.getEventEntityById(eventId);
        Comment existingComment = getComponentEntityById(commentId);

        if (existingUser != existingComment.getAuthor()) {
            throw new DataIntegrityViolationException("Пользователь не является автором комментария");
        }

        if (!existingComment.getEvent().getId().equals(eventId)) {
            throw new NotFoundException("Данный комментарий не принадлежит этому событию");
        }

        Comment savedComment = commentMapper.updateComment(updateComment, existingComment);

        log.info("Comment(id = {}) updated: old = {}, new = {}", commentId, existingComment, savedComment);
        return commentMapper.toCommentDto(savedComment);
    }

    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        User existingUser = userService.getUserById(userId);
        userEventService.getEventEntityById(eventId);
        Comment existingComment = getComponentEntityById(commentId);

        if (existingUser != existingComment.getAuthor()) {
            throw new DataIntegrityViolationException("Пользователь не является автором комментария");
        }

        if (!existingComment.getEvent().getId().equals(eventId)) {
            throw new NotFoundException("Данный комментарий не принадлежит этому событию");
        }

        commentRepository.delete(existingComment);
        log.info("Comment({}) deleted", existingComment);
    }

    public Comment getComponentEntityById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(COMMENT_NOT_FOUND.formatted(commentId)));
    }
}
