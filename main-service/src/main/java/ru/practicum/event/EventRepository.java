package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.enumState.EventState;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT * FROM events WHERE initiator_id = :userId ORDER BY id LIMIT :size OFFSET :from", nativeQuery = true)
    List<Event> getEvetByUserFromAndSize(
            @Param("userId") Long userId,
            @Param("from") Long from,
            @Param("size") Long size);

    @Query("SELECT e FROM Event e JOIN FETCH e.initiator i WHERE i.id = :userId AND e.id = :eventId")
    Event getEvetByUser(@Param("userId") Long userId,
                        @Param("eventId") Long eventId);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (CAST(:start AS timestamp) IS NULL OR e.eventDate >= :start) " +
            "AND (CAST(:end AS timestamp) IS NULL OR e.eventDate <= :end)")
    Page<Event> getEventsByAdmin(@Param("users") List<Long> users,
                                 @Param("states") List<EventState> states,
                                 @Param("categories") List<Long> categories,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 Pageable pageable);


    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR " +
            "   LOWER(e.annotation) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%')) OR " +
            "   LOWER(e.description) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(:start AS timestamp) IS NULL OR e.eventDate >= :start) " +
            "AND (CAST(:end AS timestamp) IS NULL OR e.eventDate <= :end) " +
            "AND (:onlyAvailable = false OR e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)")
    Page<Event> getEventsWithFilter(@Param("text") String text,
                                           @Param("categories") List<Long> categories,
                                           @Param("paid") boolean paid,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("onlyAvailable") boolean onlyAvailable,
                                           Pageable pageable);
}
