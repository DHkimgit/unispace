package com.project.unispace.domain.reservation.entity;

import com.project.unispace.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationTimeSlot extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_ID")
    private ReservationPolicy reservationPolicy;

    private LocalTime startTime;
    private LocalTime endTime;

    private ReservationTimeSlot(ReservationPolicy reservationPolicy, LocalTime startTime, LocalTime endTime) {
        this.reservationPolicy = reservationPolicy;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ReservationTimeSlot createTimeSlot(ReservationPolicy reservationPolicy, LocalTime startTime, LocalTime endTime){
        return new ReservationTimeSlot(reservationPolicy, startTime, endTime);
    }

}
