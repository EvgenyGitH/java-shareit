package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Create new User");
        return userService.create(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Get all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Get user by Id");
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto update(//@RequestHeader("X-ShareIt-User-Id") Long id,
                          @PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        log.info("update User");
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUserById(//@RequestHeader("X-ShareIt-User-Id") Long id,
                                  @PathVariable Long userId) {
        log.info("Delete User by id {}", userId);
        return userService.deleteUserById(userId);
    }

}
