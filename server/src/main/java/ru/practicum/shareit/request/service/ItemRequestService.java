package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createPost(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDtoWithItem> itemRequestsByRequestor(Long userId);

    List<ItemRequestDtoWithItem> itemRequestsAll(Long userId, Integer from, Integer size);

    ItemRequestDtoWithItem getItemRequestById(Long userId, Long itemRequestId);
}
