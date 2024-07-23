package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.Reservation;
import com.project.unispace.domain.reservation.entity.ReservationTimeSlot;
import com.project.unispace.domain.reservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {

    @Query("SELECT DISTINCT r From Reservation r " +
            "JOIN FETCH r.timeSlot t " +
            "JOIN FETCH r.room room " +
            "JOIN fetch room.building b " +
            "WHERE r.id = :reserveId"
    )
    Reservation getReservationById(@Param("reserveId") Long reserveId);

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.timeSlot t " +
            "JOIN FETCH t.reservationPolicy rp " +
            "JOIN FETCH rp.room room " +
            "WHERE room.id =:roomId " +
            "AND r.reservationDate BETWEEN :startDate AND :endDate")
    List<Reservation> findReservationsBetweenDates(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}