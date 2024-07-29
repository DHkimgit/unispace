package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.Reservation;
import com.project.unispace.domain.reservation.entity.Room;

import java.util.List;

public interface ReservationCustomRepository {
    Reservation findClosestReservationAfterToday(Long userId);
    List<Reservation> findUpcomingReservationsByUserId(Long userId);
    List<Reservation> findCanceledOrCompletedReservationsByUserId(Long userId);
    List<Reservation> findRejectedReservationsByUserId(Long userId);
    List<Reservation> findPendingReservationsByUserId(Long userId);


}
