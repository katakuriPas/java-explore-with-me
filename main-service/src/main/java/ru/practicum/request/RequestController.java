package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {

        log.info("PostMapping: createRequest userId = {}, eventId = {}",
                userId, eventId);

        return requestsService.createRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable Long userId) {
        log.info("GetMapping: getRequestsByUser userId = {}", userId);

        return requestsService.getRequestsByUser(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {

        log.info("PatchMapping: cancelRequest userId = {}, requestId = {}",
                userId, requestId);

        return requestsService.cancelRequest(userId, requestId);
    }
}
