package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemMapper {
    ItemDto makeToDto(Item item);

    Item makeToItem(ItemDto itemDto);

    ItemDtoWithBookingsAndComments makeToItemDtoWithBookingsAndComments(Item item, List<CommentDto> commentsDtos, BookingDtoShort lastBooking, BookingDtoShort nextBooking);
}
