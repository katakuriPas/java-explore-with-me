package ru.practicum.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("@PostMapping: createUser newUserRequest = {})", newUserRequest);
        return userService.createUser(newUserRequest);
    }

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("@GetMapping: getUser  ids = {}, from = {}, size = {})", ids, from, size);
        return userService.getUsers(ids ,from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deleteUser(@PathVariable Long userId) {
        log.info("DeleteMapping(\"/{userId}\"): deleteUser userId = {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/all")
    public List<UserDto> getAllUser() {
        return userService.findAllUsers();
    }
}
