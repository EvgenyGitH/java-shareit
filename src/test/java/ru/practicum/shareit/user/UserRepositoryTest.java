package ru.practicum.shareit.user;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class UserRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        user = createUserTest();


    }

    @Test
    void verifyBootstrappingByPersistingUser() {
        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());
    }

    @Test
    void verifyRepositoryByPersistingUser() {
        Assertions.assertNull(user.getId());
        userRepository.save(user);
        Assertions.assertNotNull(user.getId());
    }

    @Test
    public void findByEmailContainingIgnoreCase() {
        em.persist(user);
        Optional<User> result = userRepository.findByEmailContainingIgnoreCase("userTest@yamail.com");
        assertNotNull(user);
        assertEquals(result.get(), user);

    }


    private User createUserTest() {
        User user = new User();
        user.setName("UserNameTest");
        user.setEmail("userTest@yamail.com");
        return user;
    }

    private Item createItemTest() {
        Item item = new Item();
        item.setName("ItemTest");
        item.setDescription("ItemDescriptionTest");
        item.setAvailable(true);
        item.setOwner(new User(1L, "UserNameTest", "userTest@yamail.com"));
        item.setRequest(null);
        return item;
    }

}
