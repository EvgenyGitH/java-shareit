package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto makeToDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public static Item makeToItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .name(itemDto.getName())
                //    .request(itemDto.getRequest())
                .build();
    }

    public static ItemDtoWithBookingsAndComments makeToItemDtoWithBookingsAndComments(Item item, List<CommentDto> commentsDtos, BookingDtoShort lastBooking, BookingDtoShort nextBooking) {
        ItemDtoWithBookingsAndComments itemDto = new ItemDtoWithBookingsAndComments();
        if (item.getId() != null) {
            itemDto.setId(item.getId());
        }
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (lastBooking != null) {
            itemDto.setLastBooking(lastBooking);
        } else {
            itemDto.setLastBooking(null);
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(nextBooking);
        } else {
            itemDto.setNextBooking(null);
        }
        itemDto.setComments(commentsDtos);
        return itemDto;
    }

}

