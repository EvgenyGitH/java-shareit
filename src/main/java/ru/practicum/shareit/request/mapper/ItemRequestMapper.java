package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestMapper {

    ItemRequest makeToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto makeToDto(ItemRequest itemRequest);

    ItemRequestDtoWithItem makeToItemRequestWithItem(ItemRequest itemRequest, List<ItemDto> items);
}
