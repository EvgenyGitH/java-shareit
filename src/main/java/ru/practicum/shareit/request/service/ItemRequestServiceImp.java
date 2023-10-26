package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j

public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;


    public ItemRequestServiceImp(ItemRequestRepository itemRequestRepository, ItemRequestMapper itemRequestMapper, UserRepository userRepository, ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRequestMapper = itemRequestMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemRequestDto createPost(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User ID: " + userId + " not found"));
        ItemRequest itemRequest = itemRequestMapper.makeToItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.makeToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDtoWithItem> itemRequestsByRequestor(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID: " + userId + " not found");
        }


        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId);
      /*  if (itemRequestList.isEmpty()) {
            throw new RequestNotFoundException("User ID:" + userId + "has no requests");
        }*/
        List<Long> listRequestId = itemRequestList.stream()
                .map(ItemRequest::getId).collect(Collectors.toList());

        List<Item> itemsToRequest = itemRepository.findAllByRequestIdIn(listRequestId);
        List<ItemDto> itemDtoToRequest;
        if (itemsToRequest.isEmpty()) {
            itemDtoToRequest = new ArrayList<>();
        } else {
            itemDtoToRequest = itemsToRequest.stream()
                    .map(item -> itemMapper.makeToDto(item))
                    .collect(Collectors.toList());
        }

        return itemRequestList.stream()
                .map(itemRequest -> itemRequestMapper.makeToItemRequestWithItem(itemRequest, itemDtoToRequest))
                .sorted(Comparator.comparing(ItemRequestDtoWithItem::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoWithItem> itemRequestsAll(Long userId, Integer from, Integer size) {
        Sort sortBy = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of(from / size, size, sortBy);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID: " + userId + " not found");
        }
        Map<Long, ItemRequest> itemRequestMap = itemRequestRepository.findAllByRequestorIdNot(userId, pageable).stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        List<Long> listRequestId = itemRequestMap.keySet().stream().collect(Collectors.toList());

        Map<Long, List<ItemDto>> itemDtoToRequest = itemRepository.findAllByRequestIdIn(listRequestId).stream()
                .map(itemMapper::makeToDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequestMap.values().stream()
                .map(itemRequest -> itemRequestMapper.makeToItemRequestWithItem(itemRequest,
                        itemDtoToRequest.getOrDefault(itemRequest.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoWithItem getItemRequestById(Long userId, Long itemRequestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID: " + userId + " not found");
        }

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new RequestNotFoundException("User ID:" + userId + "has no requests"));

        List<Item> itemsToRequest = itemRepository.findAllByRequestId(itemRequest.getId());
        List<ItemDto> itemDtoToRequest;
        if (itemsToRequest.isEmpty()) {
            itemDtoToRequest = new ArrayList<>();
        } else {
            itemDtoToRequest = itemsToRequest.stream()
                    .map(item -> itemMapper.makeToDto(item)).collect(Collectors.toList());
        }

        return itemRequestMapper.makeToItemRequestWithItem(itemRequest, itemDtoToRequest);
    }
}
