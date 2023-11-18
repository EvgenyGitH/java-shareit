package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> json;

    @Autowired
    JacksonTester<ItemDtoWithBookingsAndComments> jsonW;

    @Test
    void testDto() throws IOException {
        ItemDto itemDto = new ItemDto(1L, "ItemTest", "ItemDescriptionTest", true, null);
        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("ItemTest");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("ItemDescriptionTest");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.requestId").isEqualTo(null);
    }

    @Test
    void testItemDtoWithBookingsAndComments() throws IOException {
        ItemDtoWithBookingsAndComments itemDtoWithBookingsAndComments = new ItemDtoWithBookingsAndComments();
        itemDtoWithBookingsAndComments.setId(1L);
        itemDtoWithBookingsAndComments.setName("ItemTest");
        itemDtoWithBookingsAndComments.setDescription("ItemDescriptionTest");
        itemDtoWithBookingsAndComments.setAvailable(true);
        itemDtoWithBookingsAndComments.setLastBooking(null);
        itemDtoWithBookingsAndComments.setNextBooking(null);
        itemDtoWithBookingsAndComments.setComments(new ArrayList<>());

        JsonContent<ItemDtoWithBookingsAndComments> result = jsonW.write(itemDtoWithBookingsAndComments);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("ItemTest");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("ItemDescriptionTest");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
    }

}