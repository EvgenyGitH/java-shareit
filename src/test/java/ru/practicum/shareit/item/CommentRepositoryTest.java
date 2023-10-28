package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class CommentRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    CommentRepository commentRepository;
    User author;
    User user;
    Item item;
    Comment comment;

    @BeforeEach
    void setUp() {
        author = createAuthorTest();
        user = createUserTest();
        item = createItemTest();
        comment = createCommentTest();
    }

    @Test
    void verifyBootstrappingByPersistingComment() {
        em.persist(author);
        em.persist(user);
        em.persist(item);
        Assertions.assertNull(comment.getId());
        em.persist(comment);
        Assertions.assertNotNull(comment.getId());
    }

    @Test
    void verifyRepositoryByPersistingComment() {
        em.persist(author);
        em.persist(user);
        em.persist(item);
        Assertions.assertNull(comment.getId());
        commentRepository.save(comment);
        Assertions.assertNotNull(comment.getId());
    }

    @Test
    public void findAllByItemIdIn() {
        em.persist(author);
        em.persist(user);
        em.persist(item);
        commentRepository.save(comment);
        List<Comment> comments = commentRepository.findAllByItemIdIn(List.of(item.getId()));
        assertNotNull(comments);
        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    @Test
    public void findAllByItemId() {
        em.persist(author);
        em.persist(user);
        em.persist(item);
        commentRepository.save(comment);
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertNotNull(comments);
        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    private User createAuthorTest() {
        User user = new User();
        user.setName("AuthorNameTest");
        user.setEmail("AuthorTest@yamail.com");
        return user;
    }

    private User createUserTest() {
        User user = new User();
        user.setName("UserNameTest");
        user.setEmail("UserTest@yamail.com");
        return user;
    }

    private Item createItemTest() {
        Item item = new Item();
        item.setName("ItemTest");
        item.setDescription("ItemDescriptionTest");
        item.setAvailable(true);
        item.setOwner(new User(2L, "UserNameTest", "UserTest@yamail.com"));
        item.setRequest(null);
        return item;
    }

    private Comment createCommentTest() {
        Comment comment = new Comment();
        comment.setText("commentTest");
        comment.setItem(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "UserNameTest", "UserTest@yamail.com"), null));
        comment.setAuthor(new User(1L, "AuthorNameTest", "AuthorTest@yamail.com"));
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

}


