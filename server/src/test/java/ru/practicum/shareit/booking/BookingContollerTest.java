package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingContollerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;

    BookingDto bookingDto;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void beforeEach() {
        bookingDto = createBookingDtoTest();
    }

    @Test
    public void createBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
        verify(bookingService).createBooking(bookingDto, 1L);
    }

    @Test
    public void approveBooking() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
        verify(bookingService).approveBooking(1L, 1L, true);
    }

    @Test
    public void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
        verify(bookingService).getBookingById(1L, 1L);
    }

    @Test
    public void getAllBookingsByUserId() throws Exception {
        when(bookingService.getAllBookingsByUserId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
        verify(bookingService).getAllBookingsByUserId(1L, "ALL", 0, 10);
    }

    @Test
    public void getAllBookingsByUserIdFromPaginationParamException() throws Exception {
        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Should be: From >= 0 and size > 0")));
    }

    @Test
    public void getAllBookingsByUserIdSizePaginationParamException() throws Exception {
        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Should be: From >= 0 and size > 0")));
    }

    @Test
    public void getAllBookingsByOwnerId() throws Exception {
        when(bookingService.getAllBookingsByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
        verify(bookingService).getAllBookingsByOwnerId(1L, "ALL", 0, 10);

    }

    @Test
    public void getAllBookingsByOwnerIdFromPaginationParamException() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Should be: From >= 0 and size > 0")));
    }

    @Test
    public void getAllBookingsByOwnerIdSizePaginationParamException() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Should be: From >= 0 and size > 0")));
    }

    private BookingDto createBookingDtoTest() {
        return BookingDto.builder()
                .id(1L)
                .item(new ItemDto(1L, "ItemNameTest", "ItemDescriptionTest", true, null))
                .booker(new UserDto(1L, "UserNameTest", "userTest@yamail.com"))
                .start(LocalDateTime.of(2023, 11, 11, 12, 00))
                .end(LocalDateTime.of(2023, 11, 12, 12, 00))
                .status(Status.WAITING)
                .build();
    }

}
