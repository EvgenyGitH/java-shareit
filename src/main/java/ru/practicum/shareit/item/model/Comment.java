package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder
@Data
public class Comment {
    private Long id;
    private String message;
    private Item item;
    private User author;
    private LocalDateTime created;
}
