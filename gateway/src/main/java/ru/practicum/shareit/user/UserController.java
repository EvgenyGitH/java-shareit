package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/users")

public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Create new User");
        return userClient.create(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Get user by Id");
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(//@RequestHeader("X-ShareIt-User-Id") Long id,
                                         @PathVariable Long userId,
                                         @RequestBody UserDto userDto) {
        log.info("update User");
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(//@RequestHeader("X-ShareIt-User-Id") Long id,
                                                 @PathVariable Long userId) {
        log.info("Delete User by id {}", userId);
        return userClient.deleteUserById(userId);
    }

}
