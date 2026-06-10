package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.DuplicatedDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = userMapper.toEntityByShort(newUserRequest);

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Email '{}' уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот email уже используется");
        }

        User savedUser = userRepository.save(user);

        return userMapper.toFullDtoEntity(savedUser);
    }

    public List<UserDto> getUsersFromAndSize(Long from, Long size) {
        List<User> userDtos = userRepository.getUsers(from, size);
        return userDtos.stream()
                .map(userMapper::toFullDtoEntity)
                .toList();
    }

    public List<UserDto> findAllUsers() {
        log.info("Запрос на получение всех пользователей. Количество: {}", userRepository.findAll().size());
        List<User> users = userRepository.findAllByOrderByIdAsc();
        return users.stream()
                .map(userMapper::toFullDtoEntity)
                .toList();
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User с id = " + userId + " не найден");
        }
        log.info("Получен запрос на удаление пользователя с id {}", userId);
        userRepository.deleteById(userId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с id = " + userId + " не найден"));
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: email не указан или не содержит @");
            throw new ValidationException("Email должен быть указан и содержать @");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Ошибка валидации: имя не указано");
            throw new ValidationException("Имя должно быть указано");
        }
    }
}

