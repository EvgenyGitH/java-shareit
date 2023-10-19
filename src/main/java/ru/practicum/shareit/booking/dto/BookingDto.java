package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;


@Builder
@Data
public class BookingDto {
    Long id;
    @JsonProperty(access = WRITE_ONLY)
    Long itemId;
    ItemDto item;
    UserDto booker;
    @FutureOrPresent
    @NotNull
    LocalDateTime start;
    @FutureOrPresent
    @NotNull
    LocalDateTime end;
    Status status;  // WAITING - APPROVED - REJECTED - CANCELED
}
