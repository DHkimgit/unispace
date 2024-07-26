package com.project.unispace.domain.reservation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor
public class ReservationPolicy extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    @JsonIgnore
    private Room room;

    // 같은 학생이 아니어도 예약이 가능한가 ?
    private boolean isOpenToPublic;
    // 예약 시 관리자의 승인이 필요한가?
    private boolean requireApproval;
    // 한번에 예약할 수 있는 시간 => 예약 타임 자동 생성이 가능한가?
    private Integer maxReservationHours;
    // 시설 예약 시작 시간
    private LocalTime openTime;
    // 시설 예약 종료 시간
    private LocalTime reserveCloseTime;
    // 며칠 전부터 예약이 가능한지
    private Integer minDaysBeforeReservation;
    //college 제한이 존재하는가?
    private boolean collegeRestrict;
    //department 제한이 존재하는가?
    private boolean departmentRestrict;

    // 예약 가능 시간대 자동 생성
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reservationPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationTimeSlot> timeSlots = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "RESERVATION_DAYS_POLICY", joinColumns = @JoinColumn(name = "RESERVATION_POLICY_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "AVAILABLE_DAYS")
    private Set<DayOfWeek> availableDays = new HashSet<>();

    @OneToMany(mappedBy = "reservationPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CollegePolicy> collegePolicies = new HashSet<>();


    @OneToMany(mappedBy = "reservationPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DepartmentPolicy> departmentPolicies = new HashSet<>();

    //=====연관관계 편의 메서드=====//
    public void addCollegeRestriction(College college) {
        CollegePolicy collegePolicy = CollegePolicy.createPolicy(this, college);
        this.collegePolicies.add(collegePolicy);
    }

    public void addDepartmentRestriction(Department department) {
        DepartmentPolicy departmentPolicy = DepartmentPolicy.createPolicy(this, department);
        this.departmentPolicies.add(departmentPolicy);
    }

    public void addAvailableDayPolicy(Set<DayOfWeek> availableDays) {
        if(!this.availableDays.isEmpty()) this.availableDays.clear();
        this.availableDays = availableDays;
    }

    //=====정적 생성 메서드=====//

    private ReservationPolicy(Room room, boolean isOpenToPublic, boolean requireApproval, Integer maxReservationHours, LocalTime openTime, LocalTime reserveCloseTime, Integer minDaysBeforeReservation) {
        this.room = room;
        this.isOpenToPublic = isOpenToPublic;
        this.requireApproval = requireApproval;
        this.maxReservationHours = maxReservationHours;
        this.openTime = openTime;
        this.reserveCloseTime = reserveCloseTime;
        this.minDaysBeforeReservation = minDaysBeforeReservation;
        this.departmentRestrict = false;
        this.collegeRestrict = false;
    }

    public static ReservationPolicy createPolicy(Room room, boolean isOpenToPublic, boolean requireApproval, Integer maxReservationHours, LocalTime openTime, LocalTime reserveCloseTime, Integer minDaysBeforeReservation){
        return new ReservationPolicy(room, isOpenToPublic, requireApproval, maxReservationHours, openTime, reserveCloseTime, minDaysBeforeReservation);
    }


    //=====비즈니스 로직=====//

    // 시설 예약 시작 시간, 한 타임에 최대 예약 가능한 시간, 시설 예약 종료 시간을 이용해서 예약 시간 슬롯 데이터를 생성
//    public void generateTimeSlot() {
//        timeSlots.clear();
//        LocalTime currentTime = openTime;
//        int maxIterations = 24; // 안전장치: 최대 24시간(1일)로 제한
//        int iteration = 0;
//
//        while ((currentTime.isBefore(reserveCloseTime) || currentTime.equals(reserveCloseTime))
//                && iteration < maxIterations) {
//            LocalTime endTime = currentTime.plusHours(maxReservationHours);
//
//            if (endTime.isAfter(reserveCloseTime)) {
//                endTime = reserveCloseTime;
//            }
//
//            if (currentTime.equals(endTime) || endTime.equals(LocalTime.of(0, 0, 0))) break;
//
//            ReservationTimeSlot timeSlot = ReservationTimeSlot.createTimeSlot(this, currentTime, endTime);
//            timeSlots.add(timeSlot);
//            System.out.println("currentTime = " + currentTime + " endTime = " + endTime + " iter = " + iteration);
//            currentTime = endTime;
//            iteration++;
//
//        }
//
//        timeSlots.sort(Comparator.comparing(ReservationTimeSlot::getStartTime));
//    }

    public void generateTimeSlot() {
        timeSlots.clear();
        LocalTime currentTime = openTime;

        if (reserveCloseTime.equals(LocalTime.of(0, 0, 0))) {
            reserveCloseTime = LocalTime.of(23, 59, 59); // 자정은 하루의 끝으로 설정
        }

        while ((currentTime.isBefore(reserveCloseTime) || currentTime.equals(reserveCloseTime))) {
            if (currentTime.plusHours(maxReservationHours).isBefore(currentTime)) break;

            LocalTime endTime = currentTime.plusHours(maxReservationHours);

            if (endTime.isAfter(reserveCloseTime)) {
                endTime = reserveCloseTime;
            }

            if (currentTime.equals(endTime) || endTime.equals(LocalTime.of(0, 0, 0))) break;

            ReservationTimeSlot timeSlot = ReservationTimeSlot.createTimeSlot(this, currentTime, endTime);
            timeSlots.add(timeSlot);
            currentTime = endTime;

        }

        if(reserveCloseTime.equals(LocalTime.of(23, 59, 59))) {
            ReservationTimeSlot timeSlot = ReservationTimeSlot.createTimeSlot(this, currentTime, LocalTime.of(0, 0, 0));
            timeSlots.add(timeSlot);
        }

        timeSlots.sort(Comparator.comparing(ReservationTimeSlot::getStartTime));

    }

    public void setDepartmentPolicies(Set<DepartmentPolicy> departmentPolicies) {
        this.departmentPolicies = departmentPolicies;
        this.departmentRestrict = true;
    }
    public void addDepartmentPolicies(DepartmentPolicy departmentPolicy) {
        this.departmentPolicies.add(departmentPolicy);
    }
    public void setCollegePolicies(Set<CollegePolicy> collegePolicies) {
        this.collegePolicies = collegePolicies;
        this.collegeRestrict = true;
    }

}
