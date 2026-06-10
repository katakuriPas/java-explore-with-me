package ru.practicum.request.model;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private String status;
}
