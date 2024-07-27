package com.project.unispace.domain.reservation.controller;

import com.project.unispace.domain.reservation.dto.ReservationDto;
import com.project.unispace.domain.reservation.dto.ReservationDto.ReservationResponses;
import com.project.unispace.domain.reservation.entity.Reservation;
import com.project.unispace.domain.reservation.service.ReservationService;
import com.project.unispace.domain.reservation.service.RoomService;
import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.entity.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationController {
    private final ReservationService reservationService;
    private final RoomService roomService;

    @PostMapping("/reservation")
    public ResponseEntity<?> makeReservation(@RequestBody ReservationDto.reservationRequest request, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(new Result<> (200, "ok", reservationService.makeReservation(request, user)));
    }

    @GetMapping("/reservation/{roomId}")
    public ResponseEntity<?> checkExistReservation(@PathVariable Integer roomId) {
        System.out.println("roomId = " + roomId);
        return ResponseEntity.ok(new Result<> (200, "ok", reservationService.checkExistReservation(roomId.longValue())));
    }

    @GetMapping("/reservation/schedule/{roomId}")
    public ResponseEntity<?> getAvailableRoomSchedule(@PathVariable Integer roomId) {
        return ResponseEntity.ok(new Result<> (200, "ok", reservationService.getAvailableRoom(roomId.longValue())));
    }

    @GetMapping("/reservation/information/{roomId}")
    public ResponseEntity<?> getAvailableRoomInformation(@PathVariable Integer roomId) {
        return ResponseEntity.ok(new Result<> (200, "ok", reservationService.getRoomInformation(roomId.longValue())));
    }

    @GetMapping("/reservation/rooms")
    public ResponseEntity<?> getAllAvailableRoomToUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(new Result<> (200, "ok", roomService.getAllRoomByUser(user)));
    }

    @GetMapping("/reservation/rooms/top3")
    public ResponseEntity<?> getThreeAvailableRoomToUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(new Result<> (200, "ok", roomService.getThreeRoomByUser(user)));
    }

    @GetMapping("/reservation/user/latest")
    public ResponseEntity<?> getClosestReservationAfterToday(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        Reservation response = reservationService.getClosestReservation(user.getId());

        if (response != null) {
            ReservationDto.LatestReservationResponse result = reservationService.getClosestReservationResponse(response, user.getId());
            return ResponseEntity.ok(new Result<>(200, "ok", result));
        } else {
            return ResponseEntity.ok(new Result<>(200, "ok", null)); // 예약이 없을 경우 null 반환
        }
    }

    @GetMapping("/reservation/user/upcoming")
    public ResponseEntity<?> getUpcomingReservations(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        List<Reservation> upcomingReservations = reservationService.getUpcomingReservations(user.getId());
        //System.out.println("upcomingReservations = " + upcomingReservations.getFirst().getId());
        if (upcomingReservations != null) {
            List<ReservationDto.ReservationResponses> result = reservationService.getUpcomingReservationsResponse(upcomingReservations, user.getId());
            return ResponseEntity.ok(new Result<>(200, "ok", result));
        } else {
            return ResponseEntity.ok(new Result<>(200, "ok", null)); // 예약이 없을 경우 null 반환
        }
    }

    @GetMapping("/reservation/user/rejected")
    public ResponseEntity<?> getRejectedReservations(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        List<Reservation> rejectedReservations = reservationService.getRejectedReservations(user.getId());

        if (rejectedReservations != null) {
            List<ReservationDto.ReservationResponses> result = reservationService.getRejectedReservationsResponse(rejectedReservations, user.getId());
            return ResponseEntity.ok(new Result<>(200, "ok", result));
        } else {
            return ResponseEntity.ok(new Result<>(200, "ok", null)); // 예약이 없을 경우 null 반환
        }
    }

    @GetMapping("/reservation/user/pending")
    public ResponseEntity<?> getPendingReservations(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        List<Reservation> pendingReservations = reservationService.getPendingReservations(user.getId());

        if (pendingReservations != null) {
            List<ReservationDto.ReservationResponses> result = reservationService.getPendingReservationsResponse(pendingReservations, user.getId());
            return ResponseEntity.ok(new Result<>(200, "ok", result));
        } else {
            return ResponseEntity.ok(new Result<>(200, "ok", null)); // 예약이 없을 경우 null 반환
        }
    }

    @GetMapping("/reservation/user/canceledOrCompleted")
    public ResponseEntity<?> getCanceledOrCompletedReservations(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        List<Reservation> canceledOrCompletedReservations = reservationService.getCanceledOrCompletedReservations(user.getId());

        if (canceledOrCompletedReservations != null) {
            List<ReservationDto.ReservationResponses> result = reservationService.getCanceledOrCompletedReservationsResponse(canceledOrCompletedReservations, user.getId());
            return ResponseEntity.ok(new Result<>(200, "ok", result));
        } else {
            return ResponseEntity.ok(new Result<>(200, "ok", null)); // 예약이 없을 경우 null 반환
        }
    }

    /*
    * 예약 취소
    * */
    @PutMapping("/reservation/cancel/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Integer reservationId) {
        reservationService.cancelReservation(reservationId.longValue());
        return ResponseEntity.ok(new Result<>(200, "ok", null));
    }

    /*
    * 거절된 예약 대상으로 관리자에게 문의 전송
    * */
    @PostMapping("/reservation/inquiry")
    public ResponseEntity<?> createReservationInquiry(@RequestBody ReservationDto.InquiryCreateRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(new Result<>(200, "ok", reservationService.createInquiry(request, user)));
    }

    /*
    * 상세 예약 정보 반환
    * */
    @GetMapping("/reservation/specific/{reservationId}")
    public ResponseEntity<?> getSpecificReservationData(@PathVariable Integer reservationId) {
        return ResponseEntity.ok(new Result<>(200, "ok", reservationService.specificReservationData(reservationId.longValue())));
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int status;
        private String message;
        private T data;

        public Result(int status, String message){
            this.status = status;
            this.message = message;
        }
    }
}
