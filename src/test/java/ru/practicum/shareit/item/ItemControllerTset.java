package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTset {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemDto = createTestItemDto();
    }

    @Test
    public void createItem() throws Exception {
        when(itemService.createItem(any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService).createItem(itemDto, 1L);
    }

    @Test
    public void getItemById() throws Exception {
        ItemDtoWithBookingsAndComments itemDtoWithBandC = createItemDtoWithBookingsAndComments();
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBandC);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBandC.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBandC.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBandC.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBandC.getAvailable())));
        verify(itemService, times(1)).getItemById(1L, 2L);
    }

    @Test
    public void getAllItemsOfUser() throws Exception {
        ItemDtoWithBookingsAndComments itemDtoWithBandC = createItemDtoWithBookingsAndComments();
        List<ItemDtoWithBookingsAndComments> itemsList = List.of(itemDtoWithBandC);
        when(itemService.getAllItemsOfUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemsList);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithBandC.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithBandC.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithBandC.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithBandC.getAvailable())));
        verify(itemService).getAllItemsOfUser(1L, 0, 10);
    }

    @Test
    public void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService).updateItem(1L, itemDto, 1L);
    }

    @Test
    public void deleteItemById() throws Exception {
        doNothing().when(itemService).deleteItemById(anyLong(), anyLong());
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(itemService, times(1)).deleteItemById(anyLong(), anyLong());
    }

    @Test
    public void searchItem() throws Exception {
        List<ItemDto> itemDtoList = List.of(itemDto);
        when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(itemDtoList);
        mvc.perform(get("/items/search")
                        //   .header("X-Sharer-User-Id", 1)
                        .param("text", "test")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
        verify(itemService).searchItem("test", 0, 10);
    }

    @Test
    public void postComment() throws Exception {
        CommentDto commentDto = createTestComment();
        when(itemService.postComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
        verify(itemService).postComment(1L, 1L, commentDto);
    }

    private ItemDto createTestItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test item");
        itemDto.setDescription("Test item description");
        itemDto.setAvailable(true);
        return itemDto;
    }

    private ItemDtoWithBookingsAndComments createItemDtoWithBookingsAndComments() {
        ItemDtoWithBookingsAndComments itemDtoWithBookingsAndComments = new ItemDtoWithBookingsAndComments();
        itemDtoWithBookingsAndComments.setId(2L);
        itemDtoWithBookingsAndComments.setName("itemTest ");
        itemDtoWithBookingsAndComments.setDescription("itemTest description");
        itemDtoWithBookingsAndComments.setAvailable(true);
        itemDtoWithBookingsAndComments.setLastBooking(null);
        itemDtoWithBookingsAndComments.setNextBooking(null);
        itemDtoWithBookingsAndComments.setComments(new ArrayList<>());
        return itemDtoWithBookingsAndComments;
    }

    private CommentDto createTestComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("CommentTest");
        commentDto.setAuthorName("UserNameTest");
        commentDto.setCreated(LocalDateTime.of(2023, 10, 10, 12, 0));
        return commentDto;
    }

}
