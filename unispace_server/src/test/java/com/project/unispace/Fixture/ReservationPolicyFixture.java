package com.project.unispace.Fixture;

import com.project.unispace.domain.reservation.entity.*;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class ReservationPolicyFixture {
    public static ReservationPolicy createPolicy(Room room, College college, Department department) {
        ReservationPolicy policy = ReservationPolicy.createPolicy(room, true, false, 1,
            LocalTime.of(8, 0, 0), LocalTime.of(20, 0, 0), 3);

        CollegePolicy collegePolicy = CollegePolicy.createPolicy(policy, college);
        Set<CollegePolicy> collegePolicies = new HashSet<>();
        collegePolicies.add(collegePolicy);
        policy.setCollegePolicies(collegePolicies);

        DepartmentPolicy departmentPolicy = DepartmentPolicy.createPolicy(policy, department);
        Set<DepartmentPolicy> departmentPolicies = new HashSet<>();
        departmentPolicies.add(departmentPolicy);
        policy.setDepartmentPolicies(departmentPolicies);

        Set<DayOfWeek> availableDays = new HashSet<>();
        availableDays.add(DayOfWeek.MONDAY);
        availableDays.add(DayOfWeek.TUESDAY);
        availableDays.add(DayOfWeek.WEDNESDAY);
        availableDays.add(DayOfWeek.THURSDAY);
        availableDays.add(DayOfWeek.FRIDAY);
        policy.addAvailableDayPolicy(availableDays);

        policy.generateTimeSlot();

        return policy;
    }
}
