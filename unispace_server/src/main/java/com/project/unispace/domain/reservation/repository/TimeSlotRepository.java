package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.ReservationTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeSlotRepository extends JpaRepository<ReservationTimeSlot, Long> {
    @Query("SELECT t FROM ReservationTimeSlot t " +
            "WHERE t.reservationPolicy.id = :policyId")
    List<ReservationTimeSlot> getReservationTimeSlotsByReservationPolicyId(@Param("policyId") Long policyId);
}
