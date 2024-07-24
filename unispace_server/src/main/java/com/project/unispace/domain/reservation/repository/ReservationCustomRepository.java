package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.Reservation;

import java.util.List;

public interface ReservationCustomRepository {
    Reservation findClosestReservationAfterToday(Long userId);
    List<Reservation> findUpcomingReservationsByUserId(Long userId);
}
