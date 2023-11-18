package ru.practicum.shareit.ItemRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class ItemRequestRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    User requestor;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requestor = createRequestorTest();
        itemRequest = createItemRequestTest();
    }

    @Test
    void verifyBootstrappingByPersistingItemRequest() {
        em.persist(requestor);
        Assertions.assertNull(itemRequest.getId());
        em.persist(itemRequest);
        Assertions.assertNotNull(itemRequest.getId());
    }

    @Test
    void verifyRepositoryByPersistingItemRequest() {
        em.persist(requestor);
        Assertions.assertNull(itemRequest.getId());
        itemRequestRepository.save(itemRequest);
        Assertions.assertNotNull(itemRequest.getId());
    }

    @Test
    public void findAllByRequestorIdOrderByCreatedAsc() {
        em.persist(requestor);
        itemRequestRepository.save(itemRequest);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(requestor.getId());
        assertNotNull(itemRequests);
        assertThat(itemRequests, hasSize(1));
        assertThat(itemRequests, hasItem(itemRequest));
    }

    @Test
    public void findAllByRequestorIdNot() {
        User user = createUserTest();
        ItemRequest itemRequest2 = createItemRequestTest2();
        em.persist(requestor);
        em.persist(user);
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequest2);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(requestor.getId(), PageRequest.of(0, 10)).toList();
        assertNotNull(itemRequests);
        assertThat(itemRequests, hasSize(1));
        assertThat(itemRequests, hasItem(itemRequest2));
    }


    private User createRequestorTest() {
        User user = new User();
        user.setName("RequestorNameTest");
        user.setEmail("RequestorTest@yamail.com");
        return user;
    }

    private User createUserTest() {
        User user = new User();
        user.setName("UserNameTest");
        user.setEmail("UserTest@yamail.com");
        return user;
    }

    private ItemRequest createItemRequestTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("ItemRequestDescription");
        itemRequest.setRequestor(new User(1L, "RequestorNameTest", "RequestorTest@yamail.com"));
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    private ItemRequest createItemRequestTest2() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("ItemRequestDescription");
        itemRequest.setRequestor(new User(2L, "UserNameTest", "UserTest@yamail.com"));
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }
}

