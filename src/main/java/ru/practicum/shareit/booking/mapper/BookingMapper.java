package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingMapper {
    BookingDto makeToDto(Booking booking);

    BookingDtoShort makeToDtoShort(Booking booking);

    Booking makeToBooking(BookingDto bookingDto);
}
