package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDtoWithBookingsAndComments getItemById(Long itemId, Long userId);

    List<ItemDtoWithBookingsAndComments> getAllItemsOfUser(Long userId, Integer from, Integer size);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    void deleteItemById(Long itemId, Long userId);

    List<ItemDto> searchItem(String text, Integer from, Integer size);

    void checkOwnerOfItem(Long itemId, Long userId, String expMessage);

    Item updateItemFields(Item updateItem);

    CommentDto postComment(Long itemId, Long userId, CommentDto commentDto);

}
