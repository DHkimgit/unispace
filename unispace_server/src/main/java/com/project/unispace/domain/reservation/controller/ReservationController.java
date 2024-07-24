package com.project.unispace.domain.reservation.controller;

import com.project.unispace.domain.reservation.dto.ReservationDto;
import com.project.unispace.domain.reservation.entity.Reservation;
import com.project.unispace.domain.reservation.service.ReservationService;
import com.project.unispace.domain.reservation.service.RoomService;
import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.entity.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        //reservationService.checkExistReservation(roomId.longValue())
    }

    @GetMapping("/reservation/check/{roomId}")
    public ResponseEntity<?> getAvailableRoomSchedule(@PathVariable Integer roomId) {
        return ResponseEntity.ok(new Result<> (200, "ok", reservationService.getAvailableRoom(roomId.longValue())));
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

        if (upcomingReservations != null) {
            List<ReservationDto.UpcomingReservationResponse> result = reservationService.getUpcomingReservationsResponse(upcomingReservations, user.getId());
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
