package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class ItemMapperImp implements ItemMapper {

    @Override
    public ItemDto makeToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                //       .request(item.getRequest())
                .build();
    }

    @Override
    public Item makeToItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .name(itemDto.getName())
                //      .request(itemDto.getRequest())
                .build();
    }

    @Override
    public ItemDtoWithBookingsAndComments makeToItemDtoWithBookingsAndComments(Item item, List<CommentDto> commentsDtos, BookingDtoShort lastBooking, BookingDtoShort nextBooking) {
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

