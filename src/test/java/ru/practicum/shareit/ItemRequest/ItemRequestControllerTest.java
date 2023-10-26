package ru.practicum.shareit.ItemRequest;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;

    ItemRequestDto itemRequestDto;
    ItemRequestDtoWithItem itemRequestDtoWithItem;

    @BeforeEach
    void beforeEach() {
        itemRequestDto = createTestItemRequestDto();
        itemRequestDtoWithItem = createTestItemRequestDtoWithItem();
    }

    @Test
    public void createPost() throws Exception {
        when(itemRequestService.createPost(any(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SS")))));
        verify(itemRequestService).createPost(itemRequestDto, 1L);
    }

    @Test
    public void itemRequestsByRequestor() throws Exception {
        when(itemRequestService.itemRequestsByRequestor(anyLong()))
                .thenReturn(List.of(itemRequestDtoWithItem));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(itemRequestService).itemRequestsByRequestor(1L);
    }

    @Test
    public void itemRequestsAll() throws Exception {


        when(itemRequestService.itemRequestsAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoWithItem));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(itemRequestService).itemRequestsAll(1L, 0, 10);
    }

    @Test
    public void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoWithItem);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoWithItem.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoWithItem.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDtoWithItem.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SS")))));
        verify(itemRequestService).getItemRequestById(1L, 1L);
    }

    //-----
    public ItemRequestDto createTestItemRequestDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("ItemRequestDescriptionTest");
        itemRequestDto.setCreated(LocalDateTime.of(2023, 10, 10, 12, 00));
        return itemRequestDto;
    }
    public ItemRequestDtoWithItem createTestItemRequestDtoWithItem() {
        ItemRequestDtoWithItem itemRequestDtoWithItem = new ItemRequestDtoWithItem();
        itemRequestDtoWithItem.setId(1L);
        itemRequestDtoWithItem.setDescription("ItemRequestDescriptionTest");
        itemRequestDtoWithItem.setCreated(LocalDateTime.of(2023, 10, 10, 12, 00));
        itemRequestDtoWithItem.setItems(new ArrayList<>());
        return itemRequestDtoWithItem;
    }


}
