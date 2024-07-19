package com.project.unispace.domain.reservation.entity;

import com.project.unispace.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Facility extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "ROOM_FACILITY_ID")
    private Long id;

    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    private Facility(String name, String description, Room room) {
        this.name = name;
        this.description = description;
        this.room = room;
    }

    public static Facility createFacility(String name, String description, Room room) {
        return new Facility(name, description, room);
    }
}
