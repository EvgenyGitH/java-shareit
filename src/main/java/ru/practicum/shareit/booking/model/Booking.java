package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Component
@Getter
@Setter
public class Booking {
    Long id;
    LocalDateTime starTime;
    LocalDateTime endTime;
    Item item;
    User booker;
    Status status;  // WAITING - APPROVED - REJECTED - CANCELED

}
