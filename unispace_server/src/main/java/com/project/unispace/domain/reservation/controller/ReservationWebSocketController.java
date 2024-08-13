package com.project.unispace.domain.reservation.controller;

import com.project.unispace.domain.reservation.dto.ReservationDto;
import com.project.unispace.domain.reservation.dto.ReservationDto.CountUpdateMessage;
import com.project.unispace.domain.reservation.dto.ReservationDto.LockUpdateMessage;
import com.project.unispace.domain.reservation.dto.ReservationDto.ReservationLockResponse;
import com.project.unispace.domain.reservation.dto.ReservationDto.TimeSlotLockRequest;
import com.project.unispace.domain.reservation.service.ReservationService;
import com.project.unispace.domain.reservation.service.ReservationWebSocketConnectionService;
import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.dto.UserDto;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ReservationWebSocketController {
    private final UserService userService;
    private final ReservationService reservationService;
    private final ReservationWebSocketConnectionService connectionManager;

    @MessageMapping("/reservations/{roomId}/subscribe")
    @SendTo("/topic/reservations/{roomId}")
    public CountUpdateMessage subscribeRoom(@DestinationVariable Long roomId) {
        int count = connectionManager.subscribe(roomId);
        System.out.println("count = " + count);
        return new CountUpdateMessage(roomId, count);
    }

    @MessageMapping("/reservations/{roomId}/leave")
    @SendTo("/topic/reservations/{roomId}")
    public CountUpdateMessage leaveRoom(@DestinationVariable Long roomId) {
        int count = connectionManager.unsubscribe(roomId);
        System.out.println("count = " + count);
        return new CountUpdateMessage(roomId, count);
    }

    @MessageMapping("/reservation/{roomId}/lock")
    @SendTo("/topic/reservations/{roomId}")
    public LockUpdateMessage lockTimeSlot(@DestinationVariable Long roomId, TimeSlotLockRequest request) {
        User user = userService.getUserById(request.getUserId());

        boolean checkLocked = reservationService.isTimeSlotLocked(roomId, request.getReserveDate(), request.getTimeSlotId());
        System.out.println("checkLocked = " + checkLocked);

        if(checkLocked) { // 선택한 시간대가 이미 선택될 시간대인 경우 true를 반환
            return new LockUpdateMessage(
                    "LOCK_UPDATE",
                    roomId,
                    request.getUserId(),
                    request.getReserveDate(),
                    request.getTimeSlotId(),
                    request.getCurrentTimeSlotId(),
                    true,
                    "이미 선점된 시간대입니다"
            );
        }
        else {
            // 시간대 선점
            reservationService.lockTimeSlot(roomId, request.getReserveDate(), request.getTimeSlotId(), user.getId());

            return new LockUpdateMessage(
                    "LOCK_UPDATE",
                    roomId,
                    request.getUserId(),
                    request.getReserveDate(),
                    request.getTimeSlotId(),
                    request.getCurrentTimeSlotId(),
                    false,
                    "시간대 선점 성공"
            );
        }
    }

    @MessageMapping("/reservation/{roomId}/unlock")
    @SendTo("/topic/reservations/{roomId}")
    public LockUpdateMessage unlockTimeSlot(@DestinationVariable Long roomId, TimeSlotLockRequest request) {
        reservationService.unlockTimeSlot(roomId, request.getReserveDate(), request.getTimeSlotId());
        System.out.println("request = " + request);
        return new LockUpdateMessage(
                "UNLOCK_UPDATE",
                roomId,
                request.getUserId(),
                request.getReserveDate(),
                request.getTimeSlotId(),
                request.getCurrentTimeSlotId(),
                false,
                "시간대 락 해제 성공"
        );
    }
}
