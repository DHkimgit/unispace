package com.project.unispace.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReservationRedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean lockTimeSlot(String roomId, String reservationDate, String timeSlotId, String userId, long lockTime) {
        String lockKey = String.format("room:%s:date:%s:timeslot:%s", roomId, reservationDate, timeSlotId);
        System.out.println("lockKey = " + lockKey);
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, userId, lockTime, TimeUnit.SECONDS);
        System.out.println("result = " + result);
        return result;
    }

    public void unlockTimeSlot(String roomId, String reservationDate, String timeSlotId) {
        String lockKey = String.format("room:%s:date:%s:timeslot:%s", roomId, reservationDate, timeSlotId);
        redisTemplate.delete(lockKey);
    }

    public String getTimeSlotLockOwner(String roomId, String reservationDate, String timeSlotId) {
        String lockKey = String.format("room:%s:date:%s:timeslot:%s", roomId, reservationDate, timeSlotId);
        return redisTemplate.opsForValue().get(lockKey);
    }

    /*
    * 락 갱신 기능
    * */
    public boolean renewLock(String roomId, String reservationDate, String timeSlotId, String userId, long lockTime) {
        String lockKey = String.format("room:%s:date:%s:timeslot:%s", roomId, reservationDate, timeSlotId);
        String currentOwner = redisTemplate.opsForValue().get(lockKey);
        if (userId.equals(currentOwner)) {
            return Boolean.TRUE.equals(redisTemplate.expire(lockKey, lockTime, TimeUnit.SECONDS));
        }
        return false;
    }
}
