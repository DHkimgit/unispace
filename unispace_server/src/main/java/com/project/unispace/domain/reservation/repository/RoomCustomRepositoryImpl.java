package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.*;
import com.project.unispace.domain.university.entity.QUniversity;
import com.project.unispace.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoomCustomRepositoryImpl implements RoomCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Room> findAllRoomAvailableToUser(User user) {
        QRoom room = QRoom.room;
        QBuilding building = QBuilding.building;
        QUniversity university = QUniversity.university;
        QReservationPolicy reservationPolicy = QReservationPolicy.reservationPolicy;
        QCollegePolicy collegePolicy = QCollegePolicy.collegePolicy;
        QDepartmentPolicy departmentPolicy = QDepartmentPolicy.departmentPolicy;

        return jpaQueryFactory
                .selectFrom(room)
                .leftJoin(room.reservationPolicy, reservationPolicy).fetchJoin()
                .leftJoin(reservationPolicy.collegePolicies, collegePolicy).fetchJoin()
                .leftJoin(reservationPolicy.departmentPolicies, departmentPolicy).fetchJoin()
                .leftJoin(room.building, building).fetchJoin()
                .leftJoin(building.university, university).fetchJoin()
                .where(
                        university.eq(user.getUniversity())
                                .and(room.isAvailable.isTrue())
                                .and(
                                        reservationPolicy.collegeRestrict.isFalse()
                                                .or(reservationPolicy.collegeRestrict.isTrue()
                                                        .and(collegePolicy.college.eq(user.getCollege()))
                                                )
                                                .or(reservationPolicy.departmentRestrict.isFalse()
                                                        .or(reservationPolicy.departmentRestrict.isTrue()
                                                                .and(departmentPolicy.department.eq(user.getDepartment()))
                                                        )
                                                )
                                )
                )
                .fetch();
    }

    @Override
    public List<Room> findThreeRoomAvailableToUser(User user) {
        QRoom room = QRoom.room;
        QBuilding building = QBuilding.building;
        QUniversity university = QUniversity.university;
        QReservationPolicy reservationPolicy = QReservationPolicy.reservationPolicy;
        QCollegePolicy collegePolicy = QCollegePolicy.collegePolicy;
        QDepartmentPolicy departmentPolicy = QDepartmentPolicy.departmentPolicy;

        return jpaQueryFactory
                .selectFrom(room)
                .leftJoin(room.reservationPolicy, reservationPolicy).fetchJoin()
                .leftJoin(reservationPolicy.collegePolicies, collegePolicy).fetchJoin()
                .leftJoin(reservationPolicy.departmentPolicies, departmentPolicy).fetchJoin()
                .leftJoin(room.building, building).fetchJoin()
                .leftJoin(building.university, university).fetchJoin()
                .where(
                        university.eq(user.getUniversity())
                                .and(room.isAvailable.isTrue())
                                .and(
                                        reservationPolicy.collegeRestrict.isFalse()
                                                .or(reservationPolicy.collegeRestrict.isTrue()
                                                        .and(collegePolicy.college.eq(user.getCollege()))
                                                )
                                                .or(reservationPolicy.departmentRestrict.isFalse()
                                                        .or(reservationPolicy.departmentRestrict.isTrue()
                                                                .and(departmentPolicy.department.eq(user.getDepartment()))
                                                        )
                                                )
                                )
                )
                .limit(3)
                .fetch();
    }
}
