package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1,'%'))" +
            "or upper(i.description) like upper(concat('%', ?1,'%'))" +
            "and i.available = true")
    List<Item> searchItem(String text, Pageable pageable);

    List<Item> findAllByOwnerId(Long userId, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> listRequestId);

    List<Item> findAllByRequestId(Long userId);
}
