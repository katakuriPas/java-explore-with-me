package ru.practicum.location;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto(Location entity);

    @Mapping(target = "id", ignore = true)
    Location toEntity(LocationDto dto);
}
