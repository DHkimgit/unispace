package com.project.unispace.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.unispace.domain.reservation.entity.InquiryStatus;
import com.project.unispace.domain.reservation.entity.ReservationStatus;
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
    public static class ReservationResponses {
        private Long reservationId;
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
        private Integer member;

        private ReservationStatus status;

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

    @Data
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class InquiryCreateResponse {
        private Long inquiryId;
        private Long reservationId;
        private Long userId;
        private InquiryStatus status;
        private String contents;
    }

    @Data
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class InquiryCreateRequest {
        private Long reservationId;
        private Long userId;
        private String contents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotLockRequest {
        private Long userId;
        private Long roomId;
        private LocalDate reserveDate;
        private Long timeSlotId;
        private Long currentTimeSlotId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationUpdateMessage {
        private String type = "RESERVATION_UPDATE";
        private Long roomId;
        private LocalDate reserveDate;
        private Long timeSlotId;

        public ReservationUpdateMessage(Long roomId, LocalDate reserveDate, Long timeSlotId) {
            this.type = "RESERVATION_UPDATE";
            this.roomId = roomId;
            this.reserveDate = reserveDate;
            this.timeSlotId = timeSlotId;
        }
    }
    @Data
    @NoArgsConstructor
    public static class LockUpdateMessage {
        private String type;
        private Long roomId;
        private Long userId;
        private LocalDate reserveDate;
        private Long timeSlotId;
        private Long formerTimeSlotId;
        private boolean isLocked;
        private String message;

        public LockUpdateMessage(String type, Long roomId, Long userId, LocalDate reserveDate, Long timeSlotId, Long formerTimeSlotId, boolean isLocked, String message) {
            this.type = type;
            this.roomId = roomId;
            this.userId = userId;
            this.reserveDate = reserveDate;
            this.timeSlotId = timeSlotId;
            this.formerTimeSlotId = formerTimeSlotId;
            this.isLocked = isLocked;
            this.message = message;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class ReservationLockResponse {
        private Long roomId;
        private LocalDate reserveDate;
        private Long timeSlotId;
        private boolean locked;
        private String message;
    }
}
