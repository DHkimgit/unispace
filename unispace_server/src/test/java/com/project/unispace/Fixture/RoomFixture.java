package com.project.unispace.Fixture;

import com.project.unispace.domain.reservation.entity.Room;
import com.project.unispace.domain.reservation.entity.Building;

public class RoomFixture {

    public static Room createRoom(Building building) {
        return Room.createRoom("101호", "강의실", building);
    }
}
