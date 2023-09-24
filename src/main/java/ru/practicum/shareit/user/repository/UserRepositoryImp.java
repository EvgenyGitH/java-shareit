package ru.practicum.shareit.user.repository;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Data
public class UserRepositoryImp implements UserRepository {

    private Long nextId = 0L;
    private final UserMapper userMapper;
    private Map<Long, User> usersRepositoryMap = new HashMap<>();


    @Override
    public User create(User user) {
        user.setId(++nextId);
        usersRepositoryMap.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return usersRepositoryMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long userId) {
        return usersRepositoryMap.get(userId);
    }

    @Override
    public User update(Long userId, User updateUser) {
        usersRepositoryMap.put(userId, updateUser);
        return updateUser;
    }

    @Override
    public User deleteUserById(Long userId) {
        return usersRepositoryMap.remove(userId);
    }


    @Override
    public boolean isUserExist(Long userId) {
        boolean flag = false;
        if (usersRepositoryMap.containsKey(userId)) {
            flag = true;
        }
        if (flag == false){
            throw new UserNotFoundException("User not found");
        }
        return flag;
    }

    @Override
    public boolean isDuplicateEmail(User user) {
        boolean flag = true;
        if(usersRepositoryMap.isEmpty()){
            flag =false;
        }
        else{
            for (User userMap : usersRepositoryMap.values()) {
                if (!userMap.getEmail().equals(user.getEmail())) {
                    flag = false;
                }else{
                    flag = true;
                    break;
                }
            }

        }
        if(flag == true){
            throw new DuplicateException("e-mail is duplicated");
        }
        return flag;
    }

    @Override
    public boolean checkEmailByUserId (Long userId, User user){
        boolean flag = true;
        if(usersRepositoryMap.isEmpty()){
            flag =false;
        }
        else{
            isUserExist(userId);
            for (User userMap : usersRepositoryMap.values()) {
                if(!userMap.getEmail().equals(user.getEmail())){
                    flag = false;
                }else if (userMap.getEmail().equals(user.getEmail()) && userMap.getId() == userId) {
                    flag = false;
                }else{
                    flag = true;
                    break;
                }
            }

        }
        if(flag == true){
            throw new DuplicateException("e-mail is used by another user");
        }
        return flag;

    }


}
