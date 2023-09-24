package ru.practicum.shareit.item.service;

import lombok.Data;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotCorrectDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    public final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        userRepository.isUserExist(userId);
        Item item = itemMapper.makeToItem(itemDto);
        item.setOwner(userRepository.getUserById(userId));
        return itemMapper.makeToDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        itemRepository.isExistItemById(itemId);
        return itemMapper.makeToDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Long userId) {
        return itemRepository.getAllItemsOfUser(userId)
                .stream()
                .map(item -> itemMapper.makeToDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        itemRepository.isExistItemById(itemId);
        userRepository.isUserExist(userId);
        checkOwnerOfItem(itemId, userId, "You can update only yours item");
        Item updateItem = itemMapper.makeToItem(itemDto);
        updateItem.setId(itemId);
        updateItem.setOwner(userRepository.getUserById(userId));
        updateItemFields(updateItem);
        return itemMapper.makeToDto(itemRepository.updateItem(itemId, updateItem));
    }

    @Override
    public ItemDto deleteItemById(Long itemId, Long userId) {
        itemRepository.isExistItemById(itemId);
        checkOwnerOfItem(itemId, userId, "You can delete only yours item");
       /* if (!itemRepository.getItemById(itemId).getOwner().equals(userRepository.getUserById(userId))){
            throw new NotCorrectDataException("You can delete only yours item");
        }*/
        return itemMapper.makeToDto(itemRepository.deleteItemById(itemId));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItem(text).stream()
                .map(item -> itemMapper.makeToDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public void checkOwnerOfItem(Long itemId, Long userId, String expMessage) {
        if (itemRepository.getItemById(itemId).getOwner().getId() != userId) {
            throw new NotCorrectDataException(expMessage);
        }
    }

    @Override
    public Item updateItemFields(Item updateItem) {
        Item itemSaved = itemRepository.getItemById(updateItem.getId());
        if (updateItem.getName() == null) {
            updateItem.setName(itemSaved.getName());
        }
        if (updateItem.getDescription() == null) {
            updateItem.setDescription(itemSaved.getDescription());
        }
        if (updateItem.getAvailable() == null) {
            updateItem.setAvailable(itemSaved.getAvailable());
        }
       /* if (updateItem.getOwner() == null) {
            updateItem.setOwner(itemSaved.getOwner());
        }*/
        if (updateItem.getRequest() == null) {
            updateItem.setRequest(itemSaved.getRequest());
        }
        return updateItem;
    }

}
