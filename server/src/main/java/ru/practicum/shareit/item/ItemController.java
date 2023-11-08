package ru.practicum.shareit.item;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@Data
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Create new User");
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingsAndComments getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long itemId) {
        log.info("Get Item by ID {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBookingsAndComments> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get all Items of Owner");
        return itemService.getAllItemsOfUser(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Update Item");
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        log.info("Delete Item by ID {}", itemId);
        itemService.deleteItemById(itemId, userId);
    }

    @GetMapping("/search")  //@GetMapping("/search?text={text}")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Search by text: {}", text);
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody CommentDto commentDto) {
        return itemService.postComment(itemId, userId, commentDto);
    }
}
