package com.project.unispace.domain.reservation.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ReservationWebSocketConnectionServiceWithHashMap {
    private final Map<Long, Integer> clientCount = new HashMap<>();

    // 구독 메서드: 클라이언트가 예약 페이지에 접속할 때 호출
    public synchronized int subscribe(Long roomId) {
        clientCount.putIfAbsent(roomId, 0);
        int newCount = clientCount.get(roomId) + 1;
        clientCount.put(roomId, newCount);
        return newCount; // 클라이언트 수 반환
    }

    // 예약 페이지에 접속한 클라이언트의 수를 반환
//    public int getCurrentClientCount(Long roomId) {
//        AtomicInteger count = clientCount.get(roomId);
//        return (count != null) ? count.get() : 0;
//    }
    public int getCurrentClientCount(Long roomId) {
        return clientCount.getOrDefault(roomId, 0);
    }
}
