package com.project.unispace.domain.reservation.entity;

import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "RESERVATION_FRIENDS")
public class ReservationFriend extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "RESERVATION_FRIEND_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESERVATION_ID")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User friend;

    private ReservationFriend(Reservation reservation, User friend) {
        this.reservation = reservation;
        this.friend = friend;
    }

    public static ReservationFriend createReservationFriend(Reservation reservation, User friend){
        return new ReservationFriend(reservation, friend);
    }


}