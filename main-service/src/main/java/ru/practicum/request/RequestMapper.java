package ru.practicum.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "created", source = "created")
    ParticipationRequestDto toRequestDto(Request request);
}
