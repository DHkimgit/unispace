package com.project.unispace.domain.reservation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CollegePolicy extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_ID")
    private ReservationPolicy reservationPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLEGE_ID")
    private College college;

    private CollegePolicy(ReservationPolicy reservationPolicy, College college) {
        this.reservationPolicy = reservationPolicy;
        this.college = college;
    }

    public static CollegePolicy createPolicy(ReservationPolicy reservationPolicy, College college) {
        return new CollegePolicy(reservationPolicy, college);
    }
}
