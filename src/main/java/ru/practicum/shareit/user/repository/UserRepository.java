package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    List<User> getAllUsers();

    User getUserById(Long userId);

    User update(Long id, User user);

    User deleteUserById(Long userId);

    boolean isUserExist(Long userId);

    boolean isDuplicateEmail(User user);

    boolean checkEmailByUserId(Long userId, User user);

}
