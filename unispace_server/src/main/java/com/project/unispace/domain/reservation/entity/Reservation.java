package com.project.unispace.domain.reservation.entity;

import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User reservedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    private LocalDate reservationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIME_SLOT_ID")
    private ReservationTimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String description;

    // 예약시 추가되는 친구 데이터 저장
    @Setter
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservationFriend> reservationFriends = new HashSet<>();

    // 예약 문의
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservationInquiry> inquiries = new ArrayList<>();

    //예약 관리한 관리자 정보
    private Long reviewAdmin;

    // 예약 승인 관련 메시지
    private String adminMessage;

    //=====생성 메서드=====//

    public Reservation(User reservedBy, Room room, LocalDate reservationDate, ReservationTimeSlot timeSlot, ReservationStatus status, String description) {
        this.reservedBy = reservedBy;
        this.room = room;
        this.reservationDate = reservationDate;
        this.timeSlot = timeSlot;
        this.status = status;
        this.description = description;
        this.reviewAdmin = null;
        this.adminMessage = null;
    }

    public static Reservation createReservation(User reservedBy, Room room, LocalDate reservationDate, ReservationTimeSlot timeSlot, String description) {
        if(room.getReservationPolicy().isRequireApproval()) {
            return new Reservation(reservedBy, room, reservationDate, timeSlot, ReservationStatus.PENDING, description);
        }
        else{
            return new Reservation(reservedBy, room, reservationDate, timeSlot, ReservationStatus.ACCEPTED, description);
        }
    }

    //=====비즈니스 로직=====//
    public ReservationFriend addFriend(User friend) {
        ReservationFriend reservationFriend = ReservationFriend.createReservationFriend(this, friend);
        this.reservationFriends.add(reservationFriend);
        return reservationFriend;
    }

    // 친구 제거 메서드
    public void removeFriend(User friend) {
        this.reservationFriends.removeIf(rf -> rf.getFriend().equals(friend));
    }


    // 예약 가능한 날짜인지 확인
    public void checkAvailable(){

    }

    // 예약 승인(관리자)
    public void acceptReservation(Long adminId, String adminMessage) {
        if(this.status.equals(ReservationStatus.PENDING)) {
            this.status = ReservationStatus.ACCEPTED;
            this.reviewAdmin = adminId;
            this.adminMessage = adminMessage;
        }
    }
    // 예약 거절(관리자)
    public void rejectReservation(Long adminId, String adminMessage) {
        if(this.status.equals(ReservationStatus.PENDING)) {
            this.status = ReservationStatus.REJECTED;
            this.reviewAdmin = adminId;
            this.adminMessage = adminMessage;
        }
    }

    //예약 취소(사용자)
    public void cancelReservation() {
        this.status = ReservationStatus.CANCELED;
    }
}
