package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.ReservationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationPolicyRepository extends JpaRepository<ReservationPolicy, Long> {
    @Query("SELECT rp FROM ReservationPolicy rp " +
            "WHERE rp.room.id = :roomId")
    ReservationPolicy findByRoomId(@Param("roomId") Long roomId);
}
