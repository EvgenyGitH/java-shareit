package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    @Query(value = "select * " +
            "from bookings as b " +
            "left join users as us on us.id = b.user_id " +
            "left join items as it on it.id = b.item_id " +
            "where us.id = ?1 " +
            "and b.start_time < ?2 and b.end_time > ?3 " +
            "order by b.start_time asc", nativeQuery = true)
    List<Booking> findAllCurrentBookingsByBookerId(Long userId, LocalDateTime now1, LocalDateTime now2, Pageable pageable);
    //List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long userId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "join b.item as it " +
            "join it.owner as own " +
            "where own.id = ?1 " +
            "and b.start < ?2 and b.end > ?3 " +
            "order by b.start desc")
    List<Booking> findAllByItemOwnerId(Long userId, LocalDateTime now1, LocalDateTime now2, Pageable pageable);
    //List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);

    List<Booking> findByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long userId, Status status, LocalDateTime now);

    List<Booking> findAllByItemIdIn(List<Long> itemIdList);

    List<Booking> findAllByItemId(Long itemId);

}
