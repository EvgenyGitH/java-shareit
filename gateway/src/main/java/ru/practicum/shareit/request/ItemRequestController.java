package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@Slf4j
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createPost(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("create Request");
        return itemRequestClient.createPost(itemRequestDto, userId);
    }


    @GetMapping
    public ResponseEntity<Object> itemRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all Requester's Item requests");
        return itemRequestClient.itemRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> itemRequestsAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get all Item requests");
        return itemRequestClient.itemRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

}
