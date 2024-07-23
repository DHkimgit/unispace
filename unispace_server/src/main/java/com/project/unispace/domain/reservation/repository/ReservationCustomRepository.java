package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.Reservation;

public interface ReservationCustomRepository {
    Reservation findClosestReservationAfterToday(Long userId);
}
