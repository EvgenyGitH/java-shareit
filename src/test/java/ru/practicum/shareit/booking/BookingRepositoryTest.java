package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class BookingRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    BookingRepository bookingRepository;
    Booking booking;
    Item item;
    User owner;
    User booker;

    @BeforeEach
    void setUp() {
        owner = createOwnerTest();
        booker = createBookerTest();
        item = createItemTest();
        booking = createBookingTest();

    }

    @Test
    void verifyBootstrappingByPersistingItem() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        Assertions.assertNull(booking.getId());
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());
    }

    @Test
    void verifyRepositoryByPersistingItem() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        Assertions.assertNull(booking.getId());
        bookingRepository.save(booking);
        Assertions.assertNotNull(booking.getId());
    }

    @Test
    public void findAllByBookerIdOrderByStartDesc() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), PageRequest.of(0, 10));
        assertNotNull(bookings);
        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(booking));
    }

    @Test
    public void findAllCurrentBookingsByBookerId() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByBookerId(booker.getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10));
        assertNotNull(bookings);
        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(booking));
    }

    @Test
    public void findAllByItemOwnerId() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(owner.getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10));
        assertNotNull(bookings);
        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(booking));
    }


    private User createOwnerTest() {
        User user = new User();
        user.setName("OwnerNameTest");
        user.setEmail("OwnerTest@yamail.com");
        return user;
    }

    private User createBookerTest() {
        User user = new User();
        user.setName("BookerNameTest");
        user.setEmail("BookerTest@yamail.com");
        return user;
    }

    private Item createItemTest() {
        Item item = new Item();
        item.setName("ItemTest");
        item.setDescription("ItemDescriptionTest");
        item.setAvailable(true);
        item.setOwner(new User(1L, "OwnerNameTest", "OwnerTest@yamail.com"));
        item.setRequest(null);
        return item;
    }

    private Booking createBookingTest() {
        Booking booking = new Booking();
        booking.setItem(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "OwnerNameTest", "OwnerTest@yamail.com"), null));
        booking.setBooker(new User(2L, "BookerNameTest", "BookerTest@yamail.com"));
        booking.setStart(LocalDateTime.of(2023, 10, 10, 12, 0));
        booking.setEnd(LocalDateTime.of(2023, 12, 12, 12, 0));
        booking.setStatus(Status.WAITING);
        return booking;
    }


}



