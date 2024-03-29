package ru.practicum.shareit.item.service;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User ID: " + userId + " not found"));
        Item item = ItemMapper.makeToItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new RequestNotFoundException("Request ID: " + userId + " not found"));
            item.setRequest(request);
        }
        return ItemMapper.makeToDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithBookingsAndComments getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item ID: " + itemId + " not found"));
        List<Comment> commentsList = commentRepository.findAllByItemId(itemId);
        List<Booking> bookingList = bookingRepository.findAllByItemId(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.makeToItemDtoWithBookingsAndComments(item, commentsList.stream()
                            .map(comment -> CommentMapper.makeToDto(comment)).collect(Collectors.toList()),
                    null, null);
        } else {
            return ItemMapper.makeToItemDtoWithBookingsAndComments(
                    item,
                    commentsList.isEmpty() ? new ArrayList<CommentDto>() : commentsList.stream()
                            .map(comment -> CommentMapper.makeToDto(comment)).collect(Collectors.toList()),
                    getLastBooking(bookingList, item.getId()),
                    getNextBooking(bookingList, item.getId()));
        }
    }

    @Override
    public List<ItemDtoWithBookingsAndComments> getAllItemsOfUser(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId, pageable);
        List<Long> itemIdList = itemList.stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        List<Booking> bookingList = bookingRepository.findAllByItemIdIn(itemIdList);
        List<Comment> commentsList = commentRepository.findAllByItemIdIn(itemIdList);

        return itemList.stream()
                .map(item -> ItemMapper.makeToItemDtoWithBookingsAndComments(
                        item,
                        commentsList.isEmpty() ? new ArrayList<CommentDto>() : commentsList.stream()
                                .filter(comment -> comment.getItem().getId().equals(item.getId()))
                                .map(comment -> CommentMapper.makeToDto(comment))
                                .collect(Collectors.toList()),
                        getLastBooking(bookingList, item.getId()),
                        getNextBooking(bookingList, item.getId())))
                .sorted(Comparator.comparing(ItemDtoWithBookingsAndComments::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException("Item ID: " + itemId + " not found");
        }
        User userFromBd = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User ID: " + userId + " not found"));
        checkOwnerOfItem(itemId, userId, "You can update only yours item");
        Item updateItem = ItemMapper.makeToItem(itemDto);
        updateItem.setId(itemId);
        updateItem.setOwner(userFromBd);
        updateItem = updateItemFields(updateItem);
        return ItemMapper.makeToDto(itemRepository.save(updateItem));
    }

    @Transactional
    @Override
    public void deleteItemById(Long itemId, Long userId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException("Item ID: " + itemId + " not found");
        }
        checkOwnerOfItem(itemId, userId, "You can delete only yours item");
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItem(text, pageable).stream()
                .map(item -> ItemMapper.makeToDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public void checkOwnerOfItem(Long itemId, Long userId, String expMessage) {
        if (!itemRepository.findById(itemId).get().getOwner().getId().equals(userId)) {
            throw new NotCorrectDataException(expMessage);
        }
    }

    @Override
    public Item updateItemFields(Item updateItem) {
        Item itemSaved = itemRepository.findById(updateItem.getId()).get();
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
        /*if (updateItem.getRequest() == null) {
            updateItem.setRequest(itemSaved.getRequest());
        }*/
        return updateItem;
    }

    @Transactional
    @Override
    public CommentDto postComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User ID: " + userId + " not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item ID: " + itemId + " not found"));
        if (bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, Status.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new IllegalOperationException("Only user who has completed booking can leave a comment");
        }
        Comment comment = CommentMapper.makeToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.makeToDto(comment);
    }


    public BookingDtoShort getLastBooking(List<Booking> bookingList, Long itemId) {
        if (bookingList.isEmpty()) {
            return null;
        } else {
            Booking lastbooking = bookingList.stream()
                    .filter(booking -> booking.getItem().getId().equals(itemId))
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            if (lastbooking == null) {
                return null;
            } else {
                return BookingMapper.makeToDtoShort(lastbooking);
            }
        }
    }

    public BookingDtoShort getNextBooking(List<Booking> bookingList, Long itemId) {
        if (bookingList.isEmpty()) {
            return null;
        } else {
            Booking nextbooking = bookingList.stream()
                    .filter(booking -> booking.getItem().getId().equals(itemId))
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            if (nextbooking == null) {
                return null;
            } else {
                return BookingMapper.makeToDtoShort(nextbooking);
            }
        }
    }

}
