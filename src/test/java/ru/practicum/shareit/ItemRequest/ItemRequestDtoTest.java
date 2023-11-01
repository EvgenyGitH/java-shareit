package ru.practicum.shareit.ItemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDto> json;
    @Autowired
    JacksonTester<ItemRequestDtoWithItem> jsonW;


    @Test
    void testDto() throws IOException {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "DescriptionRequestTest", LocalDateTime.of(2023, 10, 15, 12, 00));
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("DescriptionRequestTest");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-15T12:00:00");
    }


    @Test
    void testDtoWithItem() throws IOException {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("ItemTest");
        itemDto.setDescription("ItemDescriptionTest");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);

        ItemRequestDtoWithItem itemRequestDto = new ItemRequestDtoWithItem(1L, "DescriptionRequestTest", LocalDateTime.of(2023, 10, 15, 12, 00), List.of(itemDto));
        JsonContent<ItemRequestDtoWithItem> result = jsonW.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("DescriptionRequestTest");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-15T12:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").size().isEqualTo(1);
    }

}

