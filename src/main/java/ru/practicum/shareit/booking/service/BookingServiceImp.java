package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        Booking booking = bookingMapper.makeToBooking(bookingDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User ID: " + userId + " not found"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item ID: " + bookingDto.getItemId() + " not found"));
        if (item.getOwner().getId().equals(userId)) {
            throw new BookingNotFoundException("Owner cannot book his Item");
        }
        if (!item.getAvailable() || bookingDto.getEnd().isBefore(bookingDto.getStart())
                || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BookingNotAvailableException("Not available for booking");
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);
        return bookingMapper.makeToDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        Booking bookingFromBd = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Booking ID " + bookingId + " not found"));
        itemService.checkOwnerOfItem(bookingFromBd.getItem().getId(), userId, "The Booking can only be confirmed by the owner");

        if (!bookingFromBd.getStatus().equals(Status.WAITING)) {
            throw new IllegalOperationException("Status was confirmed by the owner earlier");
        }
        if (approved) {
            bookingFromBd.setStatus(Status.APPROVED);
        } else {
            bookingFromBd.setStatus(Status.REJECTED);
        }
        bookingFromBd = bookingRepository.save(bookingFromBd);
        return bookingMapper.makeToDto(bookingFromBd);
    }


    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking bookingFromBd = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Booking ID " + bookingId + " not found"));
        if (bookingFromBd.getBooker().getId().equals(userId) || bookingFromBd.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.makeToDto(bookingFromBd);
        } else {
            throw new BookingNotFoundException("Booking information is not available to you");
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User ID: " + userId + " not found"));
        State stateCase;
        try {
            stateCase = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalOperationException("Unknown state: " + state);
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList;
        switch (stateCase) {
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, now, now);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        }
        return bookingList.stream()
                .map(booking -> bookingMapper.makeToDto(booking))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User ID: " + userId + " not found"));
        State stateCase;
        try {
            stateCase = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalOperationException("Unknown state: " + state);
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList;
        switch (stateCase) {
            case CURRENT:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                bookingList = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        }
        return bookingList.stream()
                .map(booking -> bookingMapper.makeToDto(booking))
                .collect(Collectors.toList());
    }

}