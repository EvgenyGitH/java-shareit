package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapperImp implements BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Override
    public BookingDto makeToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(itemMapper.makeToDto(booking.getItem()))
                .booker(userMapper.makeUserDto(booking.getBooker()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    @Override
    public BookingDtoShort makeToDtoShort(Booking booking) {
        BookingDtoShort bookingDtoShort = new BookingDtoShort();
        bookingDtoShort.setId(booking.getId());
        bookingDtoShort.setBookerId(booking.getBooker().getId());
        bookingDtoShort.setStart(booking.getStart());
        bookingDtoShort.setEnd(booking.getEnd());
        bookingDtoShort.setStatus(booking.getStatus());
        return bookingDtoShort;
    }

    @Override
    public Booking makeToBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        if (bookingDto.getItem() != null) {
            booking.setItem(itemMapper.makeToItem(bookingDto.getItem()));
        }
        if (bookingDto.getBooker() != null) {
            booking.setBooker(userMapper.makeUser(bookingDto.getBooker()));
        }
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}

