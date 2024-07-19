package com.project.unispace.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.unispace.domain.reservation.entity.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
public class RoomDto {

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CreateRoom {
        private String name;
        private String description;
        private Integer BuildingId;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class RoomResponse {
        private Long buildingId;
        private String buildingName;
        private String roomName;
        private String roomDescription;
        private boolean isAvailable;
        private ReservationPolicy reservationPolicy;
    }
    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationPolicy {
        private boolean requireApproval;
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime openTime;
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime closeTime;
        private Integer maxReservationTime;
        private Set<DayOfWeek> availableDays;
        private List<ReservationTimeSlot> timeSlots;
        private List<CollegeRestrictionPolicy> collegeRestrictions;
        private List<DepartmentRestrictionPolicy> departmentRestrictions;
    }
    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CollegeRestrictionPolicy {
        private Long collegeId;
        private String collegeName;
    }
    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class DepartmentRestrictionPolicy {
        private Long departmentId;
        private String departmentName;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationTimeSlot{
        private Long slotId;
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime startTime;
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime endTime;

    }
}
