/*
package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;

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
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        UserDto savedUser = userService.create(userDto);
        assertThat(savedUser, equalTo(user));
        verify(userRepository).save(user);
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

}
*/
