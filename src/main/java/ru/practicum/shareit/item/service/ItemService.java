package ru.practicum.shareit.item.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItemsOfUser(Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto deleteItemById(Long itemId, Long userId);

    List<ItemDto> searchItem(String text);

    void checkOwnerOfItem (Long itemId, Long userId, String expMessage);
    Item updateItemFields(Item updateItem);

}
