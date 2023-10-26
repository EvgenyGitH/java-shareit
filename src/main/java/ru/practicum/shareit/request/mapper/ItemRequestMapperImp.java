package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Component
public class ItemRequestMapperImp implements ItemRequestMapper {
    @Override
    public ItemRequest makeToItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    @Override
    public ItemRequestDto makeToDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    @Override
    public ItemRequestDtoWithItem makeToItemRequestWithItem(ItemRequest itemRequest, List<ItemDto> items) {
        ItemRequestDtoWithItem itemRequestDtoWithItem = new ItemRequestDtoWithItem();
        itemRequestDtoWithItem.setId(itemRequest.getId());
        itemRequestDtoWithItem.setDescription(itemRequest.getDescription());
        itemRequestDtoWithItem.setCreated(itemRequest.getCreated());
        itemRequestDtoWithItem.setItems(items);
        return itemRequestDtoWithItem;
    }
}
