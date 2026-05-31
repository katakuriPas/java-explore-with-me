package ru.practicum.stats;

import org.mapstruct.Mapper;
import ru.practicum.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    EndpointHit toEntity(EndpointHitDto dto);

    EndpointHitDto toDto(EndpointHit entity);
}
