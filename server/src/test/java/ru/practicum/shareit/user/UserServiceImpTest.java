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

import java.util.List;
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
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> returnResult = userService.getAllUsers();
        assertThat(returnResult.get(0), equalTo(UserMapper.makeUserDto(user)));
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto returnResult = userService.getUserById(1L);
        assertThat(returnResult, equalTo(UserMapper.makeUserDto(user)));
    }

    @Test
    void getUserByIdTest_whenUserNotFound_thenUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1L));
        assertThat(exception.getMessage(), equalTo("User ID: " + 1L + " not found"));
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
                .thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.update(1L, UserMapper.makeUserDto(user)));
        assertThat(exception.getMessage(), equalTo("User ID: " + 1L + " not found"));
    }

    @Test
    void update_whenUpdateEmail_thenSaveUser() {
        UserDto updatedUser = new UserDto();
        updatedUser.setId(1L);
        updatedUser.setEmail("UpdatedUserTest@yamail.com");

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(createUserTest()));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "UserNameTest", "UpdatedUserTest@yamail.com"));

        UserDto returnResult = userService.update(1L, updatedUser);
        assertThat(returnResult, equalTo(new UserDto(1L, "UserNameTest", "UpdatedUserTest@yamail.com")));
        verify(userRepository).save(new User(1L, "UserNameTest", "UpdatedUserTest@yamail.com"));
    }

    @Test
    void update_whenUpdateName_thenSaveUser() {
        UserDto updatedUser = new UserDto();
        updatedUser.setId(1L);
        updatedUser.setName("UpdatedUserNameTest");

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(createUserTest()));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "UpdatedUserNameTest", "userTest@yamail.com"));

        UserDto returnResult = userService.update(1L, updatedUser);
        assertThat(returnResult, equalTo(new UserDto(1L, "UpdatedUserNameTest", "userTest@yamail.com")));
        verify(userRepository).save(new User(1L, "UpdatedUserNameTest", "userTest@yamail.com"));
    }

    @Test
    void deleteUserById_whenUserIdIsPresent_thenDeleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUserById(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void deleteUserById_whenUserNotFound_thenUserNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.FALSE);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(1L));
        assertThat(exception.getMessage(), equalTo("User ID: " + 1L + " not found"));
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
