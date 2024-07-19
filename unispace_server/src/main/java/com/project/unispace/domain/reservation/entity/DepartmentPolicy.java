package com.project.unispace.domain.reservation.entity;

import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.university.entity.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DepartmentPolicy extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_ID")
    private ReservationPolicy reservationPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    private Department department;

    private DepartmentPolicy(ReservationPolicy reservationPolicy, Department department) {
        this.reservationPolicy = reservationPolicy;
        this.department = department;
    }

    public static DepartmentPolicy createPolicy(ReservationPolicy reservationPolicy, Department department) {
        return new DepartmentPolicy(reservationPolicy, department);
    }
}
