package ru.practicum.shareit.ItemRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImp;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceTest {
    @InjectMocks
    ItemRequestServiceImp itemRequestService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    User owner;
    User requestor;
    Item item;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;


    @BeforeEach
    void setUp() {
        owner = createOwnerTest();
        requestor = createBookerTest();
        item = createItemTest();
        itemRequest = createItemRequestTest();
        itemRequestDto = createItemRequestDtoTest();
    }

    @Test
    void createPost_whenPostDto_thenSave() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestDto result = itemRequestService.createPost(itemRequestDto, 2L);
        assertThat(result.getId(), equalTo(1L));
    }

    @Test
    void createPost_whenUserNotFound_thenUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenThrow(UserNotFoundException.class);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.createPost(itemRequestDto, 2L));
    }

    @Test
    void itemRequestsByRequestor_whenUserId_thenListItemRequestsByRequestor() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(anyLong())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(item));
        List<ItemRequestDtoWithItem> itemRequestList = itemRequestService.itemRequestsByRequestor(2L);
        assertFalse(itemRequestList.isEmpty());
        assertThat(itemRequestList.get(0).getId(), equalTo(1L));
    }

    @Test
    void itemRequestsAll_whenUserId_thenListItemRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(itemRequestRepository.findAllByRequestorIdNot(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());
        List<ItemRequestDtoWithItem> itemRequestList = itemRequestService.itemRequestsAll(1L, 0, 10);
        assertFalse(itemRequestList.isEmpty());
        assertThat(itemRequestList.get(0).getId(), equalTo(1L));
    }

    @Test
    void getItemRequestById_whenUserId_thenItemRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        ItemRequestDtoWithItem result = itemRequestService.getItemRequestById(2L, 1L);
        assertThat(result.getId(), equalTo(1L));
    }

    @Test
    void getItemRequestById_whenItemNotAvailable_thenBookingNotAvailableException() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(itemRequestRepository.findById(anyLong()))
                .thenThrow(RequestNotFoundException.class);
        RequestNotFoundException exception = assertThrows(RequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(2L, 1L));
    }

    @Test
    void modelTest() {
        assertThat(itemRequest.getId(), equalTo(1L));
        assertThat(itemRequest.getDescription(), equalTo("DescriptionRequestTest"));
        assertThat(itemRequest.getRequestor(), equalTo(new User(2L, "RequestorNameTest", "RequestorTest@yamail.com")));
        assertThat(itemRequest.getCreated(), equalTo(LocalDateTime.of(2023, 10, 30, 12, 00)));
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

    private ItemRequest createItemRequestTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("DescriptionRequestTest");
        itemRequest.setRequestor(new User(2L, "RequestorNameTest", "RequestorTest@yamail.com"));
        itemRequest.setCreated(LocalDateTime.of(2023, 10, 30, 12, 00));
        return itemRequest;
    }

    private ItemRequestDto createItemRequestDtoTest() {
        ItemRequestDto itemRequest = new ItemRequestDto();
        itemRequest.setDescription("DescriptionRequestTest");
        return itemRequest;
    }
}
