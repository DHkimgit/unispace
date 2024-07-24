package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.*;
import com.querydsl.core.BooleanBuilder;
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
        LocalTime currentTime = LocalTime.now();

        BooleanBuilder whereClause = new BooleanBuilder()
                .and(reservation.reservedBy.id.eq(userId))
                .and(reservation.status.eq(ReservationStatus.ACCEPTED))
                .and(reservation.reservationDate.goe(current));

        if (current.equals(reservation.reservationDate)) {
            whereClause.and(timeSlot.startTime.after(currentTime));
        }

        return jpaQueryFactory.selectFrom(reservation)
                .leftJoin(reservation.room, room).fetchJoin()
                .leftJoin(reservation.timeSlot, timeSlot).fetchJoin()
                .leftJoin(room.building, building).fetchJoin()
                .where(whereClause)
                .orderBy(reservation.reservationDate.asc())
                .fetchFirst(); // 정렬된 것들 중에서 가장 처음 예약 = 가장 시간이 근접한 예약
    }

    @Override
    public List<Reservation> findUpcomingReservationsByUserId(Long userId){
        QReservation reservation = QReservation.reservation;
        QBuilding building = QBuilding.building;
        QReservationTimeSlot timeSlot = QReservationTimeSlot.reservationTimeSlot;
        QReservationFriend friends = QReservationFriend.reservationFriend;
        QRoom room = QRoom.room;

        LocalDate current = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // 기본 쿼리 생성
        BooleanBuilder whereClause = new BooleanBuilder()
                .and(reservation.reservedBy.id.eq(userId))
                .and(reservation.status.eq(ReservationStatus.ACCEPTED))
                .and(reservation.reservationDate.goe(current));  // 현재 날짜보다 크거나 같은 경우

        // reservationDate가 현재 날짜와 같은 경우에만 추가 조건
        if (current.equals(reservation.reservationDate)) {
            whereClause.and(timeSlot.startTime.after(currentTime));
        }

        return jpaQueryFactory.selectFrom(reservation)
                .leftJoin(reservation.room, room).fetchJoin()
                .leftJoin(reservation.timeSlot, timeSlot).fetchJoin()
                .leftJoin(room.building, building).fetchJoin()
                .leftJoin(reservation.reservationFriends, friends).fetchJoin()
                .where(whereClause)  // 조건 빌더 사용
                .orderBy(reservation.reservationDate.asc())
                .fetch();
    }

    @Override
    public List<Reservation> findCanceledOrCompletedReservationsByUserId(Long userId){
        QReservation reservation = QReservation.reservation;
        QBuilding building = QBuilding.building;
        QReservationTimeSlot timeSlot = QReservationTimeSlot.reservationTimeSlot;
        QReservationFriend friends = QReservationFriend.reservationFriend;
        QRoom room = QRoom.room;

        return jpaQueryFactory.selectFrom(reservation)
                .leftJoin(reservation.room, room).fetchJoin()
                .leftJoin(reservation.timeSlot, timeSlot).fetchJoin()
                .leftJoin(room.building, building).fetchJoin()
                .leftJoin(reservation.reservationFriends, friends).fetchJoin()
                .where(reservation.reservedBy.id.eq(userId)
                        .and((reservation.status.eq(ReservationStatus.COMPLETED))
                                .or(reservation.status.eq(ReservationStatus.CANCELED))))
                .orderBy(reservation.reservationDate.asc())
                .fetch();
    }

    @Override
    public List<Reservation> findRejectedReservationsByUserId(Long userId){
        QReservation reservation = QReservation.reservation;
        QBuilding building = QBuilding.building;
        QReservationTimeSlot timeSlot = QReservationTimeSlot.reservationTimeSlot;
        QReservationFriend friends = QReservationFriend.reservationFriend;
        QRoom room = QRoom.room;

        return jpaQueryFactory.selectFrom(reservation)
                .leftJoin(reservation.room, room).fetchJoin()
                .leftJoin(reservation.timeSlot, timeSlot).fetchJoin()
                .leftJoin(room.building, building).fetchJoin()
                .leftJoin(reservation.reservationFriends, friends).fetchJoin()
                .where(reservation.reservedBy.id.eq(userId)
                        .and(reservation.status.eq(ReservationStatus.REJECTED)))
                .orderBy(reservation.reservationDate.asc())
                .fetch();
    }

    @Override
    public List<Reservation> findPendingReservationsByUserId(Long userId){
        QReservation reservation = QReservation.reservation;
        QBuilding building = QBuilding.building;
        QReservationTimeSlot timeSlot = QReservationTimeSlot.reservationTimeSlot;
        QReservationFriend friends = QReservationFriend.reservationFriend;
        QRoom room = QRoom.room;

        LocalDate current = LocalDate.now();
        LocalTime current_time = LocalTime.now();

        BooleanBuilder whereClause = new BooleanBuilder()
                .and(reservation.reservedBy.id.eq(userId))
                .and(reservation.status.eq(ReservationStatus.PENDING))
                .and(reservation.reservationDate.goe(current));

        if (current.equals(reservation.reservationDate)) {
            whereClause.and(timeSlot.startTime.after(current_time));
        }

        return jpaQueryFactory.selectFrom(reservation)
                .leftJoin(reservation.room, room).fetchJoin()
                .leftJoin(reservation.timeSlot, timeSlot).fetchJoin()
                .leftJoin(room.building, building).fetchJoin()
                .leftJoin(reservation.reservationFriends, friends).fetchJoin()
                .where(whereClause)
                .orderBy(reservation.reservationDate.asc())
                .fetch();
    }
}
