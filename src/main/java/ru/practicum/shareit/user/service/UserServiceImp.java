package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.makeUser(userDto);
        userRepository.isDuplicateEmail(user);
        return userMapper.makeUserDto(userRepository.create(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(user -> userMapper.makeUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        userRepository.isUserExist(userId);
        return userMapper.makeUserDto(userRepository.getUserById(userId));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        if (userRepository.isUserExist(userId)) {
            User userMap = userRepository.getUserById(userId);
           /* if (userDto.getEmail()!=null && userDto.getEmail().equals(user.getEmail())){
                throw new DuplicateException("The email entered is the same as the previous one");
            }*/

            User updateUser = userMapper.makeUser(userDto);
            userRepository.checkEmailByUserId(userId, updateUser);

            if (updateUser.getName() == null && updateUser.getEmail() != null) {
                userRepository.checkEmailByUserId(userId, updateUser);
                updateUser.setName(userMap.getName());
            }
            if (updateUser.getEmail() == null) {
                updateUser.setEmail(userMap.getEmail());
            }

            updateUser.setId(userId);
            userRepository.update(userId, updateUser);
        }
        return getUserById(userId);
    }

    @Override
    public UserDto deleteUserById(Long userId) {
        userRepository.isUserExist(userId);
        return userMapper.makeUserDto(userRepository.deleteUserById(userId));
    }


}
