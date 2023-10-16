package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public interface CommentMapper {
    Comment makeToComment(CommentDto commentDto);

    CommentDto makeToDto(Comment comment);
}
