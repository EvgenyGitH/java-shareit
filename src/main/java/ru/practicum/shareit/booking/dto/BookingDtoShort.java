package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
public class BookingDtoShort {
    Long id;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
    Status status;

}
