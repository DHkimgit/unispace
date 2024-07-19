package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
//    @Query("SELECT r, r.building, r.reservationPolicy FROM Room r " +
//            "JOIN FETCH r.building.university u " +
//            "WHERE u.id = :univId AND r.isAvailable = true")
//    List<Room> getAllRoomByUnivId(@Param("univId") Long univId);

    @Query("SELECT DISTINCT r FROM Room r " +
            "JOIN FETCH r.building b " +
            "JOIN FETCH b.university u " +
            "JOIN FETCH r.reservationPolicy rp " +
            "LEFT JOIN FETCH rp.collegePolicies cp " +
            "LEFT JOIN FETCH cp.college " +
            "LEFT JOIN FETCH rp.departmentPolicies dp " +
            "LEFT JOIN FETCH dp.department " +
            "LEFT JOIN FETCH rp.timeSlots " +
            //"LEFT JOIN FETCH rp.availableDays " +
            "WHERE u.id = :univId AND r.isAvailable = true")
    List<Room> getAllRoomByUnivId(@Param("univId") Long univId);
}
