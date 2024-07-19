package com.project.unispace.domain.reservation.entity;

import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User reservedBy;

    private LocalDate reservationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIME_SLOT_ID")
    private ReservationTimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String description;

    // 예약시 추가되는 친구 데이터 저장
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservationFriend> reservationFriends = new HashSet<>();

    // 예약 문의
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservationInquiry> inquiries = new ArrayList<>();

    //=====비즈니스 로직=====//
    public void addFriend(User friend) {
        ReservationFriend reservationFriend = ReservationFriend.createReservationFriend(this, friend);
        this.reservationFriends.add(reservationFriend);
    }

    // 친구 제거 메서드
    public void removeFriend(User friend) {
        this.reservationFriends.removeIf(rf -> rf.getFriend().equals(friend));
    }
}
