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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotCorrectDataException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImp;
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

    Item item;
    ItemDto itemDto;
    User user;
    Comment commentOne;
    CommentDto commentDtoOne;
    Booking booking;
    ItemDtoWithBookingsAndComments itemDtoWithBAndC;

    @BeforeEach
    void setUp() {
        item = createItemTest();
        itemDto = createItemDtoTest();
        user = createUserTest();
        commentOne = CreateCommentOne();
        commentDtoOne = CreateCommentDtoOne();
        booking = createBooking();
        itemDtoWithBAndC = createItemDtoWithBookingsAndComments();

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
                .thenThrow(UserNotFoundException.class);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.createItem(itemDto, 1L));
    }

    @Test
    void getItemById_whenItemNotFound_thenItemNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, ()
                -> itemService.getItemById(1L, 1L));
    }

    @Test
    public void getAllByOwner_whenUserId_thenItemWithoutBookingsAndComments() {
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
    void searchItem_whenText_thenItemList() {
        when(itemRepository.searchItem(anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));
        List<ItemDto> items = itemService.searchItem("TeSt", 0, 10);
        assertFalse(items.isEmpty());
        assertEquals(items.get(0).getId(), 1L);
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

    private Comment CreateCommentOne() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("CommentTestOne");
        comment.setItem(new Item(1L, "ItemTest", "ItemDescriptionTest", true,
                new User(1L, "UserNameTest", "userTest@yamail.com"), null));
        comment.setAuthor(new User(2L, "UserNameTestTwo", "userTestTwo@yamail.com"));
        comment.setCreated(LocalDateTime.of(2023, 10, 31, 12, 00));
        return comment;
    }

    private CommentDto CreateCommentDtoOne() {
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
        itemDtoWithBAndC.setComments(new ArrayList<>()); //List<CommentDto> comments;
        return itemDtoWithBAndC;
    }


}


