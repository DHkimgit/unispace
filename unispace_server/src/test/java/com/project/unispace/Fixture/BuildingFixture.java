package com.project.unispace.Fixture;
import com.project.unispace.domain.reservation.entity.Building;
import com.project.unispace.domain.reservation.entity.DayOfWeek;
import com.project.unispace.domain.university.entity.University;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildingFixture {
    public static Building createBuilding(University university) {
        Building building = Building.createBuilding("인문경영관", "본관 건너편",
            6, 2,
            LocalTime.of(6, 0), LocalTime.of(23, 0));

        Set<DayOfWeek> openDays = Stream.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
            .map(DayOfWeek::valueOf)
            .collect(Collectors.toSet());

        building.setOpenDaysPolicy(openDays);
        building.changeUniversity(university);

        return building;
    }
}
