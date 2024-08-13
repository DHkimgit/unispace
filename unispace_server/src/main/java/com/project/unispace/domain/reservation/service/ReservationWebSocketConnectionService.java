package com.project.unispace.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ReservationWebSocketConnectionService {
    private final ConcurrentHashMap<Long, AtomicInteger> clientCount = new ConcurrentHashMap<>();

    // 구독 메서드: 클라이언트가 예약 페이지에 접속할 때 호출
    public int subscribe(Long roomId) {
        clientCount.putIfAbsent(roomId, new AtomicInteger(0)); // 방 ID가 없으면 초기화
        return clientCount.get(roomId).incrementAndGet(); // 클라이언트 수 증가 후 반환
    }

    // 구독 해제 메서드: 클라이언트가 예약 페이지를 이탈할 때 호출
    public int unsubscribe(Long roomId) {
        AtomicInteger currentCount = clientCount.get(roomId);
        if (currentCount != null) {
            int newCount = currentCount.decrementAndGet(); // 클라이언트 수 감소

            // 클라이언트 수가 0이 되면 방에서 제거
            if (newCount <= 0) {
                clientCount.remove(roomId);
            }
            return newCount; // 감소된 클라이언트 수 반환
        } else {
            // 방 ID가 존재하지 않으면 0 반환
            return 0;
        }
    }

    // 예약 페이지에 접속한 클라이언트의 수를 반환
    public int getCurrentClientCount(Long roomId) {
        AtomicInteger count =  clientCount.get(roomId);
        return (count != null) ? count.get() : 0;
    }
}
