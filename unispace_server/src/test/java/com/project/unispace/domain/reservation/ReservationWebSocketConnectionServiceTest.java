package com.project.unispace.domain.reservation;

import com.project.unispace.domain.reservation.service.ReservationWebSocketConnectionService;
import com.project.unispace.domain.reservation.service.ReservationWebSocketConnectionServiceWithHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ReservationWebSocketConnectionServiceTest {
    @Autowired
    private ReservationWebSocketConnectionServiceWithHashMap serviceWithHashMap;

    @Autowired
    private ReservationWebSocketConnectionService serviceWithConcurrentHashMap;

    @Test
    public void testHashMapConcurrency() throws InterruptedException {
        Long roomId = 1L;

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                serviceWithHashMap.subscribe(roomId);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                serviceWithHashMap.subscribe(roomId);
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int actualCount = serviceWithHashMap.getCurrentClientCount(roomId);
        int expectedCount = 20000;

        System.out.println("Expected Value: 20000");
        System.out.println("Final client value: " + actualCount);

        assertTrue(actualCount < expectedCount);
    }

    @Test
    public void testConcurrentHashMapConcurrency() throws InterruptedException {
        Long roomId = 2L;

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                serviceWithConcurrentHashMap.subscribe(roomId);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                serviceWithConcurrentHashMap.subscribe(roomId);
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int actualCount = serviceWithConcurrentHashMap.getCurrentClientCount(roomId);
        int expectedCount = 20000;

        System.out.println("Expected Value: 20000");
        System.out.println("Final client value: " + actualCount);

        assertEquals(expectedCount, actualCount); // ConcurrentHashMap의 경우, 실제 카운트가 예상과 일치해야 함
    }
}

