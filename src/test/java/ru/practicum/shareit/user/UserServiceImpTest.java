package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImp;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {
    @InjectMocks
    private UserServiceImp userService;
    @Mock
    private UserRepository userRepository;

    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = createUserTest();
        userDto = createUserDtoTest();
    }

    @Test
    void create_whenCreateUser_thenSaveUser() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "UserNameTest", "userTest@yamail.com"));
        User userToSave = new User();
        userToSave.setName("UserNameTest");
        userToSave.setEmail("userTest@yamail.com");

        UserDto savedUser = userService.create(userDto);
        assertThat(savedUser, equalTo(userDto));
        verify(userRepository).save(userToSave);
    }

    @Test
    void create_whenCreateUser_thenConstraintViolationException() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(ConstraintViolationException.class);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> userService.create(UserMapper.makeUserDto(user)));
    }

    @Test
    void update_whenUpdateUser_thenSaveUser() {
        UserDto updatedUser = createUpdatedUserDtoTest();
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(createUserTest()));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "UpdatedUserNameTest", "UpdatedUserTest@yamail.com"));

        UserDto returnResult = userService.update(1L, updatedUser);
        assertThat(returnResult, equalTo(updatedUser));
        verify(userRepository).save(new User(1L, "UpdatedUserNameTest", "UpdatedUserTest@yamail.com"));
    }

    @Test
    void update_whenUserNotFound_thenNotFoundException() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(UserNotFoundException.class);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.update(1L, UserMapper.makeUserDto(user)));
    }

    @Test
    void deleteUserById_whenUserIdIsPresent_thenDeleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUserById(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void checkEmailByUserId_whenUserByEmail_thenDuplicateException() {
        when(userRepository.findByEmailContainingIgnoreCase(anyString()))
                .thenReturn(Optional.of(new User(2L, "UserNameTest", "userTest@yamail.com")));
        DuplicateException exception = assertThrows(DuplicateException.class,
                () -> userService.checkEmailByUserId(user));
        assertThat(exception.getMessage(), equalTo("e-mail is duplicated"));
    }

    private User createUserTest() {
        User user = new User();
        user.setId(1L);
        user.setName("UserNameTest");
        user.setEmail("userTest@yamail.com");
        return user;
    }

    private UserDto createUserDtoTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("UserNameTest");
        userDto.setEmail("userTest@yamail.com");
        return userDto;
    }

    private UserDto createUpdatedUserDtoTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("UpdatedUserNameTest");
        userDto.setEmail("UpdatedUserTest@yamail.com");
        return userDto;
    }

}
