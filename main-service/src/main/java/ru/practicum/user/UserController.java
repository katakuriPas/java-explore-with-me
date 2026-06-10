package ru.practicum.user;

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
    public UserDto createUser(@RequestBody NewUserRequest newUserRequest) {
        log.info("@PostMapping: createUser newUserRequest = {})", newUserRequest);
        return userService.createUser(newUserRequest);
    }

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(name = "from", defaultValue = "0") Long from,
            @RequestParam(name = "size", defaultValue = "10") Long size
    ) {
        log.info("@GetMapping: getUser from = {}, size = {})", from, size);
        return userService.getUsersFromAndSize(from, size);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("DeleteMapping(\"/{userId}\"): deleteUser userId = {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/all")
    public List<UserDto> getAllUser() {
        return userService.findAllUsers();
    }
}
