package com.project.unispace.domain.reservation.entity;

import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.university.entity.University;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@RequiredArgsConstructor
public class Building extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String location;
    private Integer aboveGroundFloors;
    private Integer underGroundFloors;
    private LocalTime openTime;
    private LocalTime closeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIVERSITY_ID")
    private University university;

    @ElementCollection
    @CollectionTable(name = "BUILDING_OPEN_DAYS", joinColumns = @JoinColumn(name = "BUILDING_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "OPEN_DAY_OF_WEEK")
    private Set<DayOfWeek> openDays = new HashSet<>();

    private Building(String name, String location, Integer aboveGroundFloors, Integer underGroundFloors, LocalTime openTime, LocalTime closeTime) {
        this.name = name;
        this.location = location;
        this.aboveGroundFloors = aboveGroundFloors;
        this.underGroundFloors = underGroundFloors;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public static Building createBuilding(String name, String location, Integer aboveGroundFloors, Integer underGroundFloors, LocalTime openTime, LocalTime closeTime) {
        return new Building(name, location, aboveGroundFloors, underGroundFloors, openTime, closeTime);
    }

    public void setOpenDaysPolicy(Set<DayOfWeek> openDays) {
        this.openDays.clear();
        if (openDays != null){
            this.openDays = openDays;
        }
    }

    public void changeUniversity(University university){
        this.university = university;
    }

}
