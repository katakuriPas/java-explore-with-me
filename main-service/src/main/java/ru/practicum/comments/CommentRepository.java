package ru.practicum.comments;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.comments.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthorIdAndEventId(Long userId, Long eventId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.id IN :commentIds AND c.event.id = :eventId")
    int deleteByIdInAndEventId(
            @Param("commentIds") List<Long> commentIds,
            @Param("eventId") Long eventId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.event.id = :eventId AND c.author.id = :userId")
    int deleteCommentsByUserId(
            @Param("eventId") Long eventId,
            @Param("userId") Long userId);

    @Query("SELECT c FROM Comment c " +
            "WHERE (:eventId IS NULL OR c.event.id = :eventId) " +
            "AND (:userId IS NULL OR c.author.id = :userId) " +
            "AND (:searchStatus IS NULL OR c.status IN :searchStatus) " +
            "AND (CAST(:start AS timestamp) IS NULL OR c.createdOn >= :start) " +
            "AND (CAST(:end AS timestamp) IS NULL OR c.createdOn <= :end)")
    List<Comment> getComments(
            @Param("eventId") Long eventId,
            @Param("userId") Long userId,
            @Param("searchStatus") List<CommentStatus> searchStatus,
            @Param("start") LocalDateTime rangeStart,
            @Param("end") LocalDateTime rangeEnd,
            Pageable pageable);
}
