package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Reservation findClosestReservationAfterToday(Long userId) {
        QReservation reservation = QReservation.reservation;
        QBuilding building = QBuilding.building;
        QReservationTimeSlot timeSlot = QReservationTimeSlot.reservationTimeSlot;
        QRoom room = QRoom.room;

        LocalDate current = LocalDate.now();

        return jpaQueryFactory.selectFrom(reservation)
                .leftJoin(reservation.room, room).fetchJoin()
                .leftJoin(reservation.timeSlot, timeSlot).fetchJoin()
                .leftJoin(room.building, building).fetchJoin()
                .where(reservation.reservedBy.id.eq(userId)
                        .and(reservation.reservationDate.goe(current)))
                .orderBy(reservation.reservationDate.asc())
                .fetchFirst(); // 정렬된 것들 중에서 가장 처음 예약 = 가장 시간이 근접한 예약
    }

    @Override
    public List<Reservation> findUpcomingReservationsByUserId(Long userId){
        QReservation reservation = QReservation.reservation;
        QBuilding building = QBuilding.building;
        QReservationTimeSlot timeSlot = QReservationTimeSlot.reservationTimeSlot;
        QRoom room = QRoom.room;

        LocalDate current = LocalDate.now();
        LocalTime current_time = LocalTime.now();

        return jpaQueryFactory.selectFrom(reservation)
                .leftJoin(reservation.room, room).fetchJoin()
                .leftJoin(reservation.timeSlot, timeSlot).fetchJoin()
                .leftJoin(room.building, building).fetchJoin()
                .where(reservation.reservedBy.id.eq(userId)
                        .and(reservation.reservationDate.goe(current))
                        .and(timeSlot.startTime.after(current_time)))
                .orderBy(reservation.reservationDate.asc())
                .fetch();
    }

}
