package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImp;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {
    @InjectMocks
    ItemServiceImp itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    Item item;
    ItemDto itemDto;
    User user;
    Comment commentOne;
    CommentDto commentDtoOne;
    Booking booking;
    ItemDtoWithBookingsAndComments itemDtoWithBAndC;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        item = createItemTest();
        itemDto = createItemDtoTest();
        user = createUserTest();
        commentOne = createCommentOne();
        commentDtoOne = createCommentDtoOne();
        booking = createBooking();
        itemDtoWithBAndC = createItemDtoWithBookingsAndComments();
        itemRequest = createItemRequestTest();
    }

    @Test
    void createItem_whenCreateItem_thenSaveItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1L, "UserNameTest", "userTest@yamail.com")));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                        new User(1L, "UserNameTest", "userTest@yamail.com"), null));
        Item itemFromDto = Item.builder()
                .name("ItemTest")
                .description("ItemDescriptionTest")
                .available(true)
                .owner(new User(1L, "UserNameTest", "userTest@yamail.com"))
                .request(null)
                .build();
        ItemDto savedItem = itemService.createItem(itemDto, 1L);
        assertThat(savedItem,
                equalTo(new ItemDto(1L, "ItemTest", "ItemDescriptionTest", true, null)));
        verify(itemRepository).save(itemFromDto);
    }

    @Test
    void createItem_whenUserNotFound_thenUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.createItem(itemDto, 1L));
        assertThat(exception.getMessage(), equalTo("User ID: " + 1L + " not found"));
    }

    @Test
    void createItem_whenRequestNotFound_thenRequestNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1L, "UserNameTest", "userTest@yamail.com")));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        itemDto.setRequestId(2L);
        RequestNotFoundException exception = assertThrows(RequestNotFoundException.class,
                () -> itemService.createItem(itemDto, 1L));
        assertThat(exception.getMessage(), equalTo("Request ID: " + 1L + " not found"));
    }

    @Test
    void createItem_whenRequestNotNull_thenSaveItem() {
        itemDto.setRequestId(1L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1L, "UserNameTest", "userTest@yamail.com")));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                        new User(1L, "UserNameTest", "userTest@yamail.com"), itemRequest));
        Item itemFromDto = Item.builder()
                .name("ItemTest")
                .description("ItemDescriptionTest")
                .available(true)
                .owner(new User(1L, "UserNameTest", "userTest@yamail.com"))
                .request(itemRequest)
                .build();
        ItemDto savedItem = itemService.createItem(itemDto, 1L);
        assertThat(savedItem,
                equalTo(new ItemDto(1L, "ItemTest", "ItemDescriptionTest", true, 1L)));
        verify(itemRepository).save(itemFromDto);
    }

    @Test
    void getItemById_whenItemId_thenItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemId(anyLong())).thenReturn(new ArrayList<>());

        ItemDtoWithBookingsAndComments result = itemService.getItemById(1L, 1L);

        assertThat(result, equalTo(createItemDtoWithBookingsAndComments()));
    }

    @Test
    void getItemById_whenItemNotFound_thenItemNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, ()
                -> itemService.getItemById(1L, 1L));
        assertThat(exception.getMessage(), equalTo("Item ID: " + 1L + " not found"));
    }

    @Test
    void getItemById_whenNotOwner_thenItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemId(anyLong())).thenReturn(new ArrayList<>());

        ItemDtoWithBookingsAndComments result = itemService.getItemById(1L, 2L);
        assertThat(result, equalTo(createItemDtoWithBookingsAndComments()));
    }

    @Test
    public void getAllItemsOfUser_whenCommentsListWithComments_thenItemWithoutBookingsAndComments() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "UserNameTest", "userTest@yamail.com"), null));
        doReturn(items).when(itemRepository).findAllByOwnerId(anyLong(), any(Pageable.class));
        doReturn(new ArrayList<>()).when(bookingRepository).findAllByItemIdIn(Mockito.anyList());
        doReturn(List.of(commentOne)).when(commentRepository).findAllByItemIdIn(Mockito.anyList());

        List<ItemDtoWithBookingsAndComments> result = itemService.getAllItemsOfUser(1L, 0, 10);
        assertFalse(result.isEmpty());
        assertEquals(result.get(0).getId(), 1L);
    }

    @Test
    public void getAllItemsOfUser_whenCommentsListIsEmpty_thenItemWithoutBookingsAndComments() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "UserNameTest", "userTest@yamail.com"), null));
        doReturn(items).when(itemRepository).findAllByOwnerId(anyLong(), any(Pageable.class));
        doReturn(new ArrayList<>()).when(bookingRepository).findAllByItemIdIn(Mockito.anyList());
        doReturn(new ArrayList<>()).when(commentRepository).findAllByItemIdIn(Mockito.anyList());

        List<ItemDtoWithBookingsAndComments> result = itemService.getAllItemsOfUser(1L, 0, 10);
        assertTrue(result.get(0).getComments().isEmpty());

    }

    @Test
    void update_whenUpdateItem_thenSaveItem() {
        doReturn(Optional.of(new User(1L, "UserNameTest", "userTest@yamail.com")))
                .when(userRepository).findById(anyLong());
        doReturn(new Item(1L, "ItemTest", "NEW ItemDescriptionTest", true,
                new User(1L, "UserNameTest", "userTest@yamail.com"), null))
                .when(itemRepository).save(any(Item.class));
        doReturn(true).when(itemRepository).existsById(anyLong());
        doReturn(Optional.of(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "UserNameTest", "userTest@yamail.com"), null)))
                .when(itemRepository).findById(anyLong());

        Item itemFromDto = Item.builder()
                .id(1L)
                .name("ItemTest")
                .description("NEW ItemDescriptionTest")
                .available(true)
                .owner(new User(1L, "UserNameTest", "userTest@yamail.com"))
                .request(null)
                .build();
        ItemDto update = new ItemDto();
        update.setId(1L);
        update.setDescription("NEW ItemDescriptionTest");
        update.setRequestId(null);
        ItemDto updatedItem = itemService.updateItem(1L, update, 1L);
        assertThat(updatedItem, equalTo(
                new ItemDto(1L, "ItemTest", "NEW ItemDescriptionTest", true, null)));
        verify(itemRepository).save(itemFromDto);

    }

    @Test
    void update_whenUpdateItem_thenNotCorrectDataException() {
        doReturn(Optional.of(new User(2L, "UserNameTest", "userTest@yamail.com")))
                .when(userRepository).findById(anyLong());
        doReturn(true).when(itemRepository).existsById(anyLong());
        doReturn(Optional.of(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "UserNameTest", "userTest@yamail.com"), null)))
                .when(itemRepository).findById(anyLong());

        ItemDto update = new ItemDto();
        update.setId(1L);
        update.setDescription("NEW ItemDescriptionTest");
        update.setRequestId(null);

        NotCorrectDataException exception = assertThrows(NotCorrectDataException.class,
                () -> itemService.updateItem(1L, update, 2L));
    }

    @Test
    void update_whenItemNotFound_thenItemNotFoundException() {
        when(itemRepository.existsById(anyLong()))
                .thenReturn(false);
        ItemDto update = new ItemDto();
        update.setId(1L);
        update.setDescription("NEW ItemDescriptionTest");
        update.setRequestId(null);

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(1L, update, 2L));
        assertThat(exception.getMessage(), equalTo("Item ID: " + 1L + " not found"));
    }

    @Test
    void deleteItemById_whenItemId_thenDeleteItem() {
        when(itemRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        doNothing().when(itemRepository).deleteById(anyLong());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        itemService.deleteItemById(1L, 1L);

        verify(itemRepository).existsById(1L);
        verify(itemRepository).deleteById(1L);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void deleteItemById_whenItemNotFound_thenItemNotFoundException() {
        when(itemRepository.existsById(anyLong())).thenReturn(Boolean.FALSE);

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.deleteItemById(1L, 1L));
        assertThat(exception.getMessage(), equalTo("Item ID: " + 1L + " not found"));

    }

    @Test
    void searchItem_whenText_thenItemList() {
        when(itemRepository.searchItem(anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));
        List<ItemDto> items = itemService.searchItem("TeSt", 0, 10);
        assertFalse(items.isEmpty());
        assertEquals(items.get(0).getId(), 1L);
    }

    @Test
    void searchItem_whenTextIsEmpty_thenEmptyList() {
        List<ItemDto> items = itemService.searchItem(" ", 0, 10);
        assertTrue(items.isEmpty());
    }

    @Test
    void postComment_whenComment_thenSaveComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(commentOne);

        CommentDto result = itemService.postComment(1L, 1L, commentDtoOne);
        assertThat(result.getText(), equalTo("CommentTestOne"));
    }

    @Test
    void postComment_whenUnavailableComment_thenIllegalOperationException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        IllegalOperationException exception = assertThrows(IllegalOperationException.class,
                () -> itemService.postComment(1L, 1L, commentDtoOne));
        assertThat(exception.getMessage(), equalTo("Only user who has completed booking can leave a comment"));
    }

    @Test
    void updateItemFieldsTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Item update = Item.builder()
                .id(1L)
                .name("ItemTest")
                .description("NEW ItemDescriptionTest")
                .available(true)
                .owner(new User(1L, "UserNameTest", "userTest@yamail.com"))
                .request(null)
                .build();
        Item result = itemService.updateItemFields(update);
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("ItemTest"));
        assertThat(result.getDescription(), equalTo("NEW ItemDescriptionTest"));
        assertThat(result.getAvailable(), equalTo(true));
        assertThat(result.getOwner(), equalTo(new User(1L, "UserNameTest", "userTest@yamail.com")));
        assertThat(result.getRequest(), equalTo(null));
    }

    @Test
    void updateItemFieldsTestDescription() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Item update = Item.builder()
                .id(1L)
                .name("New ItemTest")
                .available(true)
                .owner(new User(1L, "UserNameTest", "userTest@yamail.com"))
                .request(null)
                .build();
        Item result = itemService.updateItemFields(update);
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("New ItemTest"));
        assertThat(result.getDescription(), equalTo("ItemDescriptionTest"));
        assertThat(result.getAvailable(), equalTo(true));
        assertThat(result.getOwner(), equalTo(new User(1L, "UserNameTest", "userTest@yamail.com")));
        assertThat(result.getRequest(), equalTo(null));
    }

    @Test
    void getLastBookingTest() {
        List<Booking> bookingList = List.of(booking);
        BookingDtoShort result = itemService.getLastBooking(bookingList, 1L);
        assertThat(result, equalTo(null));
    }

    @Test
    void getLastBookingTestExist() {
        booking.setStart(LocalDateTime.of(2023, 10, 10, 12, 00));
        booking.setEnd(LocalDateTime.of(2023, 10, 15, 12, 00));
        List<Booking> bookingList = List.of(booking);
        BookingDtoShort result = itemService.getLastBooking(bookingList, 1L);
        assertThat(result, equalTo(new BookingDtoShort(
                1L, 2L,
                LocalDateTime.of(2023, 10, 10, 12, 00),
                LocalDateTime.of(2023, 10, 15, 12, 00),
                Status.WAITING)));
    }

    @Test
    void getNextBookingTest() {
        List<Booking> bookingList = List.of(booking);
        BookingDtoShort result = itemService.getNextBooking(bookingList, 1L);
        assertThat(result, equalTo(new BookingDtoShort(
                1L, 2L,
                LocalDateTime.of(2023, 11, 15, 12, 00),
                LocalDateTime.of(2023, 12, 15, 12, 00),
                Status.WAITING)));
    }

    @Test
    void getNextBookingTestNotExist() {
        booking.setStart(LocalDateTime.of(2023, 10, 10, 12, 00));
        booking.setEnd(LocalDateTime.of(2023, 10, 15, 12, 00));

        List<Booking> bookingList = List.of(booking);
        BookingDtoShort result = itemService.getNextBooking(bookingList, 1L);
        assertThat(result, equalTo(null));
    }

    @Test
    void makeToItemDtoWithBookingsAndCommentsTest_whenIdNull() {
        item.setId(null);
        ItemDtoWithBookingsAndComments result = ItemMapper.makeToItemDtoWithBookingsAndComments(item, new ArrayList<>(), null, null);
        assertThat(result.getId(), equalTo(null));
    }

    @Test
    void makeToItemDtoWithBookingsAndCommentsTestLastNextBooking() {
        BookingDtoShort lastBooking = new BookingDtoShort();
        lastBooking.setId(1L);
        lastBooking.setBookerId(1L);
        lastBooking.setStart(LocalDateTime.of(2023, 10, 1, 12, 00));
        lastBooking.setEnd(LocalDateTime.of(2023, 12, 31, 12, 00));
        lastBooking.setStatus(Status.WAITING);
        BookingDtoShort nextBooking = new BookingDtoShort();
        nextBooking.setId(2L);
        nextBooking.setBookerId(2L);
        nextBooking.setStart(LocalDateTime.of(2023, 10, 1, 12, 00));
        nextBooking.setEnd(LocalDateTime.of(2023, 12, 31, 12, 00));
        nextBooking.setStatus(Status.WAITING);

        ItemDtoWithBookingsAndComments result = ItemMapper.makeToItemDtoWithBookingsAndComments(item, new ArrayList<>(), lastBooking, nextBooking);
        assertThat(result.getLastBooking().getId(), equalTo(1L));
        assertThat(result.getNextBooking().getId(), equalTo(2L));
    }

    private User createUserTest() {
        User user = new User();
        user.setId(1L);
        user.setName("UserNameTest");
        user.setEmail("userTest@yamail.com");
        return user;
    }

    private Item createItemTest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("ItemTest");
        item.setDescription("ItemDescriptionTest");
        item.setAvailable(true);
        item.setOwner(new User(1L, "UserNameTest", "userTest@yamail.com"));
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

    private Comment createCommentOne() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("CommentTestOne");
        comment.setItem(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "UserNameTest", "userTest@yamail.com"), null));
        comment.setAuthor(new User(2L, "UserNameTestTwo", "userTestTwo@yamail.com"));
        comment.setCreated(LocalDateTime.of(2023, 10, 31, 12, 00));
        return comment;
    }

    private CommentDto createCommentDtoOne() {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("CommentTestOne");
        comment.setAuthorName("UserNameTestTwo");
        comment.setCreated(LocalDateTime.of(2023, 10, 31, 12, 00));
        return comment;
    }

    private Booking createBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "UserNameTest", "userTest@yamail.com"), null));
        booking.setBooker(new User(2L, "UserNameTestTwo", "userTestTwo@yamail.com"));
        booking.setStart(LocalDateTime.of(2023, 11, 15, 12, 00));
        booking.setEnd(LocalDateTime.of(2023, 12, 15, 12, 00));
        booking.setStatus(Status.WAITING);
        return booking;
    }

    private ItemDtoWithBookingsAndComments createItemDtoWithBookingsAndComments() {
        ItemDtoWithBookingsAndComments itemDtoWithBAndC = new ItemDtoWithBookingsAndComments();
        itemDtoWithBAndC.setId(1L);
        itemDtoWithBAndC.setName("ItemTest");
        itemDtoWithBAndC.setDescription("ItemDescriptionTest");
        itemDtoWithBAndC.setAvailable(true);
        itemDtoWithBAndC.setLastBooking(null);
        itemDtoWithBAndC.setNextBooking(null);
        itemDtoWithBAndC.setComments(new ArrayList<>());
        return itemDtoWithBAndC;
    }

    private ItemRequest createItemRequestTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("DescriptionRequestTest");
        itemRequest.setRequestor(new User(2L, "RequestorNameTest", "RequestorTest@yamail.com"));
        itemRequest.setCreated(LocalDateTime.of(2023, 10, 30, 12, 00));
        return itemRequest;
    }
}


