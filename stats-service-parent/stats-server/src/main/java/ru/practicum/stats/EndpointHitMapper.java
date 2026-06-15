package ru.practicum.stats;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    @Mapping(target = "id", ignore = true)
    EndpointHit toEntity(EndpointHitDto dto);

    EndpointHitDto toDto(EndpointHit entity);
}
