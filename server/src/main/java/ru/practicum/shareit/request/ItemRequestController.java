package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createPost(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        log.info("create Request");
        return itemRequestService.createPost(itemRequestDto, userId);
    }


    @GetMapping
    public List<ItemRequestDtoWithItem> itemRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all Requester's Item requests");
        return itemRequestService.itemRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItem> itemRequestsAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all Item requests");
        return itemRequestService.itemRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItem getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

}
