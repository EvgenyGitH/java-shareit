package ru.practicum.shareit.item.repository;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Data
public class ItemRepositoryImp implements ItemRepository {
    private Long nextId = 0L;
    //  private final ItemMapper itemMapper;
    private final Map<Long, Item> itemRepositoryMap = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(++nextId);
        itemRepositoryMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepositoryMap.get(itemId);
    }

    @Override
    public List<Item> getAllItemsOfUser(Long userId) {
        return itemRepositoryMap.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        itemRepositoryMap.put(itemId, item);
        return item;
    }

    @Override
    public Item deleteItemById(Long itemId) {
        return itemRepositoryMap.remove(itemId);
    }

    @Override
    public List<Item> searchItem(String text) {

        return itemRepositoryMap.values().stream()
                .filter(item -> item.getAvailable() == true)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isExistItemById(Long itemId) {
        boolean flag = false;
        if (itemRepositoryMap.containsKey(itemId)) {
            flag = true;
        }
        if (flag == false) {
            throw new ItemNotFoundException("Item Not Found");
        }
        return flag;
    }

}
