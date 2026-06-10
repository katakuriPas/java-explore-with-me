package ru.practicum.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.request.ParticipationRequestDto;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests; // подтвержденные заявки

    private List<ParticipationRequestDto> rejectedRequests;  // отмененные заявки
}
