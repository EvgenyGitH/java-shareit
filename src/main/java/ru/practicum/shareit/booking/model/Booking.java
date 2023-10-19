package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Table(name = "bookings")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @NotNull
    Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    User booker;

    @Column(name = "start_time")
    @NotNull
    LocalDateTime start;
    @Column(name = "end_time")
    @NotNull
    LocalDateTime end;

    @Enumerated(EnumType.STRING)
    Status status;  // WAITING - APPROVED - REJECTED - CANCELED

}
