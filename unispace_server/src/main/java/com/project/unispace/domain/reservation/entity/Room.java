package com.project.unispace.domain.reservation.entity;

import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Room extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;

    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL)
    private ReservationPolicy reservationPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDING_ID")
    private Building building;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Facility> facilities = new ArrayList<>();

    private boolean isAvailable;

    //=====private 생성자=====//
    private Room(String name, String description, Building building, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.building = building;
    }

    //=====생성 메서드=====//
    public static Room createRoom(String name, String description, Building building){
        return new Room(name, description, building, true);
    }

    //=====비즈니스 로직=====//
    public void addFacility(String facilityName, String facilityDescription) {
        Facility newFacility = Facility.createFacility(facilityName, description, this);
        this.facilities.add(newFacility);
    }

    public void removeFacility(Facility facility) {
        this.facilities.removeIf(rf -> rf.getId().equals(facility.getId()));
    }

    public void definePolicy(ReservationPolicy policy){
        this.reservationPolicy = policy;
    }

    public void open() {
        this.isAvailable = true;
    }

    public void close() {
        this.isAvailable = false;
    }

}
