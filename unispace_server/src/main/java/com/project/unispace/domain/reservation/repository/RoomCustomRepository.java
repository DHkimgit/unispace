package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.Room;
import com.project.unispace.domain.user.entity.User;

import java.util.List;

public interface RoomCustomRepository {
    List<Room> findAllRoomAvailableToUser(User user);
    List<Room> findThreeRoomAvailableToUser(User user);
}
