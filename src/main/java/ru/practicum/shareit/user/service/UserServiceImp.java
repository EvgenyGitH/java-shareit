package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.makeUser(userDto);
        user = userRepository.save(user);
        return userMapper.makeUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> userMapper.makeUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        // userRepository.isUserExist(userId);
        return userMapper.makeUserDto(
                userRepository.findById(userId).orElseThrow(() ->
                        new UserNotFoundException("User ID: " + userId + " not found")));
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userFromBd = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User ID: " + userId + " not found"));
        User updateUser = userMapper.makeUser(userDto);
        updateUser.setId(userId);
        if (updateUser.getName() == null && updateUser.getEmail() != null) {
            checkEmailByUserId(updateUser);
            updateUser.setName(userFromBd.getName());
        }
        if (updateUser.getEmail() == null) {
            updateUser.setEmail(userFromBd.getEmail());
        }
        userRepository.save(updateUser);
        return userMapper.makeUserDto(updateUser);
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID: " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void checkEmailByUserId(User user) {
        Optional<User> userByEmail = userRepository.findByEmailContainingIgnoreCase(user.getEmail());
        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(user.getId())) {
            throw new DuplicateException("e-mail is duplicated");
        }
    }

}
