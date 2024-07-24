package com.project.unispace.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.unispace.domain.user.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class ReservationDto {
    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class reservationRequest {
        private Long userId;
        private Set<Long> friends;
        private Long timeSlotId;
        private LocalDate reserveDate;
        private Long roomId;
        private String description;

        public reservationRequest(){

        }

        public reservationRequest(Long userId, Set<Long> friends, Long timeSlotId, LocalDate reserveDate) {
            this.userId = userId;
            this.friends = friends;
            this.timeSlotId = timeSlotId;
            this.reserveDate = reserveDate;
        }

        public reservationRequest(Long userId, Set<Long> friends, Long timeSlotId, LocalDate reserveDate, String description) {
            this.userId = userId;
            this.friends = friends;
            this.timeSlotId = timeSlotId;
            this.description = description;
            this.reserveDate = reserveDate;
        }
    }

    @Data
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class reservationResponse {
        private Long userId;
        private Set<reservationFriend> friends;
        private Long timeSlotId;

        private LocalDate reserveDate;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime startTime;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime endTime;

        private Long roomId;
        private String buildingName;
        private String roomName;

        private String description;

    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class reservationFriend {
        private Long friendId;
        private String friendNickname;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class AvailableRoom {
        private Long roomId;
        private LocalDate availableDate;
        private List<AvailableTime> availableTimes;
        private List<UnavailableTime> unavailableTimes;

        public AvailableRoom(Long roomId, LocalDate availableDate) {
            this.roomId = roomId;
            this.availableDate = availableDate;
            this.availableTimes = new ArrayList<AvailableTime>();
            this.unavailableTimes = new ArrayList<UnavailableTime>();
        }

        public void addAvailableTime(AvailableTime availableTime) {
            this.availableTimes.add(availableTime);
        }

        public void addUnavailableTime(UnavailableTime unavailableTime) {
            this.unavailableTimes.add(unavailableTime);
        }

    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class AvailableTime {
        private Long timeSlotId;
        private LocalTime startTime;
        private LocalTime endTime;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UnavailableTime {
        private Long timeSlotId;
        private LocalTime startTime;
        private LocalTime endTime;
    }

    @Data
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class LatestReservationResponse {
        private Long userId;
        private Long timeSlotId;

        private LocalDate reserveDate;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime startTime;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime endTime;

        private Long roomId;
        private String buildingName;
        private String roomName;
    }

    @Data
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class UpcomingReservationResponse {
        private Long userId;
        private Long timeSlotId;

        private LocalDate reserveDate;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime startTime;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime endTime;

        private Long roomId;
        private String buildingName;
        private String roomName;

    }

    @Data
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class ReservationDecision {
        private Integer reservationId;
        private String message;
    }
}
