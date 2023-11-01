package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Status;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoShortTest {
    @Autowired
    JacksonTester<BookingDtoShort> json;

    @Test
    void testDto() throws IOException {

        BookingDtoShort booking = new BookingDtoShort();
        booking.setId(1L);
        booking.setBookerId(1L);
        booking.setStart(LocalDateTime.of(2023, 10, 15, 12, 00));
        booking.setEnd(LocalDateTime.of(2023, 12, 15, 12, 00));
        booking.setStatus(Status.WAITING);

        JsonContent<BookingDtoShort> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-15T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-15T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

}


