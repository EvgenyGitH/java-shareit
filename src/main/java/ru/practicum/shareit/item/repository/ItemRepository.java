package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item createItem(Item item);

    Item getItemById(Long itemId);

    List<Item> getAllItemsOfUser(Long userId);

    Item updateItem(Long itemId, Item item);

    Item deleteItemById(Long itemId);

    List<Item> searchItem(String text);

    boolean isExistItemById(Long itemId);
}
