package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;


@RequiredArgsConstructor
public class BookingMapper {

    public static BookingDto makeToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .item(ItemMapper.makeToDto(booking.getItem()))
                .booker(UserMapper.makeUserDto(booking.getBooker()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoShort makeToDtoShort(Booking booking) {
        BookingDtoShort bookingDtoShort = new BookingDtoShort();
        bookingDtoShort.setId(booking.getId());
        bookingDtoShort.setBookerId(booking.getBooker().getId());
        bookingDtoShort.setStart(booking.getStart());
        bookingDtoShort.setEnd(booking.getEnd());
        bookingDtoShort.setStatus(booking.getStatus());
        return bookingDtoShort;
    }

    public static Booking makeToBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        if (bookingDto.getItem() != null) {
            booking.setItem(ItemMapper.makeToItem(bookingDto.getItem()));
        }
        if (bookingDto.getBooker() != null) {
            booking.setBooker(UserMapper.makeUser(bookingDto.getBooker()));
        }
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}

