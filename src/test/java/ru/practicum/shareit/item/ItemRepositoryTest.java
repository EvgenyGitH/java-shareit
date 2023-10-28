package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class ItemRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRepository itemRepository;
    Item item;
    User user;

    @BeforeEach
    void setUp() {
        user = createUserTest();
        item = createItemTest();

    }

    @Test
    void verifyBootstrappingByPersistingItem() {
        em.persist(user);
        Assertions.assertNull(item.getId());
        em.persist(item);
        Assertions.assertNotNull(item.getId());
    }

    @Test
    void verifyRepositoryByPersistingItem() {
        em.persist(user);
        Assertions.assertNull(item.getId());
        itemRepository.save(item);
        Assertions.assertNotNull(item.getId());
    }

    @Test
    public void search() {
        em.persist(user);
        em.persist(item);
        List<Item> items = itemRepository.searchItem("TEST", PageRequest.of(0, 10));
        assertNotNull(items);
        assertThat(items, hasSize(1));
        assertThat(items, hasItem(item));
    }

    @Test
    void findAllByOwnerId() {
        em.persist(user);
        em.persist(item);
        List<Item> items = itemRepository.findAllByOwnerId(1L, PageRequest.ofSize(10));
        assertNotNull(items);
        assertThat(items, hasSize(1));
        assertThat(items, hasItem(item));
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
