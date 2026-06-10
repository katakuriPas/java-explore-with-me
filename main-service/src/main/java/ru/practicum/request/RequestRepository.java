package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    @Query("SELECT r FROM Request r WHERE r.requester.id = :userId")
    List<Request> findAllRequestsByUserId(Long userId);

    @Query("SELECT r FROM Request r WHERE r.requester.id = :userId AND r.id = :requestId")
    Request findByUserIdAndRequestId(Long userId, Long requestId);

    @Query("SELECT r FROM Request r WHERE r.requester.id = :userId AND r.event.id = :eventId")
    Request getRequestByUserIdAndEventId(Long userId, Long eventId);
}
