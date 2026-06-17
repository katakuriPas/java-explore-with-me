package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.CommentMapper;
import ru.practicum.comments.CommentRepository;
import ru.practicum.comments.CommentStatus;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.UpdateCommentAdminRequest;
import ru.practicum.event.service.UserEventService;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCommentService {
    private final CommentRepository commentRepository;

    private final UserCommentService userCommentService;
    private final UserEventService userEventService;
    private final UserService userService;

    private final CommentMapper commentMapper;

    public List<CommentDto> getComments(
            Long eventId,
            Long userId,
            List<String> statusList,
            Boolean isNew,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from, int size) {

        if (eventId != null) {
            userEventService.getEventEntityById(eventId);
        }

        if (userId != null) {
            userService.getUserById(userId);
        }

        List<CommentStatus> searchStatus = new ArrayList<>();
        if (statusList != null && !statusList.isEmpty()) {
            searchStatus = statusList.stream()
                    .map(status -> switch (status.toUpperCase()) {
                        case "POSITIVE" -> CommentStatus.POSITIVE;
                        case "NEUTRAL" -> CommentStatus.NEUTRAL;
                        case "NEGATIVE" -> CommentStatus.NEGATIVE;
                        default -> throw new IllegalArgumentException("Неизвестный статус комментария: " + status);
                    })
                    .toList();
        } else searchStatus = null;


        log.info("ADMIN searchStatus = {}", searchStatus);
        Sort sort = (isNew != null && isNew)
                ? Sort.by("createdOn").descending()
                : Sort.by("createdOn").ascending();

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Comment> comments = commentRepository.getComments(
                eventId, userId, searchStatus, rangeStart, rangeEnd, pageable);

        return commentMapper.toCommentDtoList(comments);
    }

    public CommentDto getCommentByEventIdAndCommentId(Long eventId, Long commentId) {
        userEventService.getEventEntityById(eventId);
        Comment existingComment = userCommentService.getComponentEntityById(commentId);

        if (!existingComment.getEvent().getId().equals(eventId)) {
            throw new NotFoundException("Данный комментарий не принадлежит этому событию");
        }

        log.info("Comments received (eventId = {}, commentId = {}): {}",
                eventId, commentId, existingComment);
        return commentMapper.toCommentDto(existingComment);
    }

    public CommentDto updateCommentStatusByAdmin(Long commentId, UpdateCommentAdminRequest updateComment) {
        Comment existingComment = userCommentService.getComponentEntityById(commentId);

        String status = updateComment.getStatus();

        CommentStatus newStatus = switch (status.toUpperCase()) {
            case "POSITIVE" -> CommentStatus.POSITIVE;
            case "NEUTRAL" -> CommentStatus.NEUTRAL;
            case "NEGATIVE" -> CommentStatus.NEGATIVE;
            default -> throw new IllegalArgumentException("Неизвестное действие: " + status);
        };
        existingComment.setStatus(newStatus);
        Comment savedComment = commentRepository.save(existingComment);

        log.info("Comment(id = {}, newStatus = {}) updatedStatusByAdmin: old = {}, new = {}",
                commentId, status, existingComment, savedComment);
        return commentMapper.toCommentDto(savedComment);
    }

    @Transactional
    public void deleteCommentsByIds(Long eventId, List<Long> commentIds) {
        userEventService.getEventEntityById(eventId);

        int deletedCount = commentRepository.deleteByIdInAndEventId(commentIds, eventId);

        if (deletedCount != commentIds.size()) {
            throw new NotFoundException("Некоторые комментарии не найдены или не относятся к событию");
        }
        log.info("Comments(eventId = {}, ids = {}) deleted", eventId, commentIds);
    }

    @Transactional
    public void deleteCommentsByUserId(Long eventId, Long userId) {
        userEventService.getEventEntityById(eventId);
        userService.getUserById(userId);

        int deletedCount = commentRepository.deleteCommentsByUserId(eventId, userId);

        if (deletedCount == 0) {
            throw new NotFoundException("Пользователь не писал комментариев под данным событием");
        }
        log.info("Comments(eventId = {}, userId = {}) deleted", eventId, userId);

    }
}
