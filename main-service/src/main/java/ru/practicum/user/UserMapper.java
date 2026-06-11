package ru.practicum.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toFullDtoEntity(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(NewUserRequest newUserRequest);
}
