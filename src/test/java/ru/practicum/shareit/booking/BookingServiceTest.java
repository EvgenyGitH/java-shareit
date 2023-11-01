package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImp;
import ru.practicum.shareit.exception.BookingNotAvailableException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.IllegalOperationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceTest {
    @InjectMocks
    BookingServiceImp bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemService itemService;

    User owner;
    User booker;
    Item item;
    ItemDto itemDto;
    Booking booking;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        owner = createOwnerTest();
        booker = createBookerTest();
        item = createItemTest();
        itemDto = createItemDtoTest();
        booking = createBookingTest();
        bookingDto = createBookingDtoTest();
    }

    @Test
    void createBooking_whenBookingDto_thenSave() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(bookingDto, 2L);
        assertThat(result.getId(), equalTo(1L));
        verify(bookingRepository).save(createBookingWithoutIdTest());
    }

    @Test
    void createBooking_whenItemNotAvailable_thenBookingNotAvailableException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        item.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class,
                () -> bookingService.createBooking(bookingDto, 2L));
    }

    @Test
    void approveBooking_whenBooking_thenApprove() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        doNothing().when(itemService).checkOwnerOfItem(anyLong(), anyLong(), anyString());
        Booking approvedBooking = new Booking();
        approvedBooking.setId(1L);
        approvedBooking.setItem(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "OwnerNameTest", "OwnerTest@yamail.com"), null));
        approvedBooking.setBooker(new User(2L, "BookerNameTest", "BookerTest@yamail.com"));
        approvedBooking.setStart(LocalDateTime.of(2023, 11, 15, 12, 00));
        approvedBooking.setEnd(LocalDateTime.of(2023, 12, 15, 12, 00));
        approvedBooking.setStatus(Status.APPROVED);
        when(bookingRepository.save(any(Booking.class))).thenReturn(approvedBooking);

        BookingDto result = bookingService.approveBooking(1L, 1L, true);
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void approveBooking_whenBookingNotFound_thenBookingNotFoundException() {
        when(bookingRepository.findById(anyLong()))
                .thenThrow(BookingNotFoundException.class);
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void getBookingById_whenBookingId_thenBookingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertThat(result.getId(), equalTo(1L));
        itemDto.setId(1L);
        assertThat(result.getItem(), equalTo(itemDto));
    }

    @Test
    void getAllBookingsByUserId_whenUserId_thenBookingDtoList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllCurrentBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingList = bookingService.getAllBookingsByUserId(2L, "CURRENT", 0, 10);
        assertFalse(bookingList.isEmpty());
        assertThat(bookingList.get(0).getId(), equalTo(1L));
    }

    @Test
    void getAllBookingsByOwnerId_whenUserId_thenBookingDtoList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingList = bookingService.getAllBookingsByOwnerId(1L, "CURRENT", 0, 10);
        assertFalse(bookingList.isEmpty());
        assertThat(bookingList.get(0).getId(), equalTo(1L));
    }

    @Test
    void getAllBookingsByOwnerId_whenUnknownState_thenIllegalOperationException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        IllegalOperationException exception = assertThrows(IllegalOperationException.class,
                () -> bookingService.getAllBookingsByOwnerId(2L, "UnKnowN", 0, 10));
    }


    private User createOwnerTest() {
        User user = new User();
        user.setId(1L);
        user.setName("OwnerNameTest");
        user.setEmail("OwnerTest@yamail.com");
        return user;
    }

    private User createBookerTest() {
        User user = new User();
        user.setId(2L);
        user.setName("BookerNameTest");
        user.setEmail("BookerTest@yamail.com");
        return user;
    }

    private Item createItemTest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("ItemTest");
        item.setDescription("ItemDescriptionTest");
        item.setAvailable(true);
        item.setOwner(new User(1L, "OwnerNameTest", "OwnerTest@yamail.com"));
        item.setRequest(null);
        return item;
    }

    private ItemDto createItemDtoTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("ItemTest");
        itemDto.setDescription("ItemDescriptionTest");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);
        return itemDto;
    }

    private Booking createBookingTest() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "OwnerNameTest", "OwnerTest@yamail.com"), null));
        booking.setBooker(new User(2L, "BookerNameTest", "BookerTest@yamail.com"));
        booking.setStart(LocalDateTime.of(2023, 10, 15, 12, 00));
        booking.setEnd(LocalDateTime.of(2023, 12, 15, 12, 00));
        booking.setStatus(Status.WAITING);
        return booking;
    }

    private BookingDto createBookingDtoTest() {
        BookingDto booking = new BookingDto();
        booking.setItemId(1L);
        booking.setItem(new ItemDto(1L, "ItemTest", "ItemDescriptionTest", true, null));
        booking.setBooker(new UserDto(2L, "BookerNameTest", "BookerTest@yamail.com"));
        booking.setStart(LocalDateTime.of(2023, 10, 15, 12, 00));
        booking.setEnd(LocalDateTime.of(2023, 12, 15, 12, 00));
        booking.setStatus(Status.WAITING);
        return booking;
    }

    private Booking createBookingWithoutIdTest() {
        Booking booking = new Booking();
        booking.setItem(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "OwnerNameTest", "OwnerTest@yamail.com"), null));
        booking.setBooker(new User(2L, "BookerNameTest", "BookerTest@yamail.com"));
        booking.setStart(LocalDateTime.of(2023, 10, 15, 12, 00));
        booking.setEnd(LocalDateTime.of(2023, 12, 15, 12, 00));
        booking.setStatus(Status.WAITING);
        return booking;
    }

}
