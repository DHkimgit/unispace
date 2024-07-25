package com.project.unispace.domain.reservation.service;

import com.project.unispace.domain.reservation.dto.BuildingDto;
import com.project.unispace.domain.reservation.dto.RoomDto;
import com.project.unispace.domain.reservation.entity.Building;
import com.project.unispace.domain.reservation.entity.ReservationPolicy;
import com.project.unispace.domain.reservation.entity.Room;
import com.project.unispace.domain.reservation.repository.BuildingRepository;
import com.project.unispace.domain.reservation.repository.RoomRepository;
import com.project.unispace.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;

    public Long createRoom(RoomDto.CreateRoom request) {
        return buildingRepository.findById(request.getBuildingId().longValue())
                .map(building -> {
                    System.out.println("building = " + building.getId());
                    Room room = Room.createRoom(request.getName(), request.getDescription(), building);
                    roomRepository.save(room);
                    return room.getId();
                }
        ).orElseThrow(() -> new EntityNotFoundException("No"));
    }

    // 대학 내부의 모든 예약 가능한 방 반환
    public List<RoomDto.RoomResponse> getAllRoomByUniv(Long univId) {
        return roomRepository.getAllRoomByUnivId(univId).stream()
                .map(room -> {
                    List<RoomDto.CollegeRestrictionPolicy> collegeRestrictionPolicies = room.getReservationPolicy().getCollegePolicies().stream()
                            .map(collegePolicy -> new RoomDto.CollegeRestrictionPolicy(collegePolicy.getCollege().getId(), collegePolicy.getCollege().getName())).toList();
                    List<RoomDto.DepartmentRestrictionPolicy> departmentRestrictionPolicies = room.getReservationPolicy().getDepartmentPolicies().stream()
                            .map(departmentPolicy -> new RoomDto.DepartmentRestrictionPolicy(departmentPolicy.getDepartment().getId(), departmentPolicy.getDepartment().getName())).toList();
                    List<RoomDto.ReservationTimeSlot> timeSlots = room.getReservationPolicy().getTimeSlots().stream()
                            .map(timeSlot -> new RoomDto.ReservationTimeSlot(timeSlot.getId(), timeSlot.getStartTime(), timeSlot.getEndTime()))
                            .sorted(Comparator.comparing(RoomDto.ReservationTimeSlot::getSlotId))
                            .toList();
                    RoomDto.ReservationPolicy reservationPolicy = new RoomDto.ReservationPolicy(room.getReservationPolicy().isRequireApproval(),
                            room.getReservationPolicy().getOpenTime(), room.getReservationPolicy().getReserveCloseTime(),
                            room.getReservationPolicy().getMaxReservationHours(), room.getReservationPolicy().getAvailableDays(),
                            timeSlots, collegeRestrictionPolicies, departmentRestrictionPolicies);
                    return new RoomDto.RoomResponse(room.getBuilding().getId(), room.getBuilding().getName(),
                            room.getId(), room.getName(), room.getDescription(), room.isAvailable(), reservationPolicy);
                }).collect(Collectors.toList());
    }

    public List<RoomDto.RoomResponse> getAllRoomByUser(User user) {
        return roomRepository.findAllRoomAvailableToUser(user).stream()
                .map(room -> {
                    List<RoomDto.CollegeRestrictionPolicy> collegeRestrictionPolicies = room.getReservationPolicy().getCollegePolicies().stream()
                            .map(collegePolicy -> new RoomDto.CollegeRestrictionPolicy(collegePolicy.getCollege().getId(), collegePolicy.getCollege().getName())).toList();
                    List<RoomDto.DepartmentRestrictionPolicy> departmentRestrictionPolicies = room.getReservationPolicy().getDepartmentPolicies().stream()
                            .map(departmentPolicy -> new RoomDto.DepartmentRestrictionPolicy(departmentPolicy.getDepartment().getId(), departmentPolicy.getDepartment().getName())).toList();
                    List<RoomDto.ReservationTimeSlot> timeSlots = room.getReservationPolicy().getTimeSlots().stream()
                            .map(timeSlot -> new RoomDto.ReservationTimeSlot(timeSlot.getId(), timeSlot.getStartTime(), timeSlot.getEndTime()))
                            .sorted(Comparator.comparing(RoomDto.ReservationTimeSlot::getSlotId))
                            .toList();
                    RoomDto.ReservationPolicy reservationPolicy = new RoomDto.ReservationPolicy(room.getReservationPolicy().isRequireApproval(),
                            room.getReservationPolicy().getOpenTime(), room.getReservationPolicy().getReserveCloseTime(),
                            room.getReservationPolicy().getMaxReservationHours(), room.getReservationPolicy().getAvailableDays(),
                            timeSlots, collegeRestrictionPolicies, departmentRestrictionPolicies);
                    return new RoomDto.RoomResponse(room.getBuilding().getId(), room.getBuilding().getName(),
                            room.getId(), room.getName(), room.getDescription(), room.isAvailable(), reservationPolicy);
                }).collect(Collectors.toList());
    }

    public List<RoomDto.RoomResponse> getThreeRoomByUser(User user) {
        return roomRepository.findThreeRoomAvailableToUser(user).stream()
                .map(room -> {
                    List<RoomDto.CollegeRestrictionPolicy> collegeRestrictionPolicies = room.getReservationPolicy().getCollegePolicies().stream()
                            .map(collegePolicy -> new RoomDto.CollegeRestrictionPolicy(collegePolicy.getCollege().getId(), collegePolicy.getCollege().getName())).toList();
                    List<RoomDto.DepartmentRestrictionPolicy> departmentRestrictionPolicies = room.getReservationPolicy().getDepartmentPolicies().stream()
                            .map(departmentPolicy -> new RoomDto.DepartmentRestrictionPolicy(departmentPolicy.getDepartment().getId(), departmentPolicy.getDepartment().getName())).toList();
                    List<RoomDto.ReservationTimeSlot> timeSlots = room.getReservationPolicy().getTimeSlots().stream()
                            .map(timeSlot -> new RoomDto.ReservationTimeSlot(timeSlot.getId(), timeSlot.getStartTime(), timeSlot.getEndTime()))
                            .sorted(Comparator.comparing(RoomDto.ReservationTimeSlot::getSlotId))
                            .toList();
                    RoomDto.ReservationPolicy reservationPolicy = new RoomDto.ReservationPolicy(room.getReservationPolicy().isRequireApproval(),
                            room.getReservationPolicy().getOpenTime(), room.getReservationPolicy().getReserveCloseTime(),
                            room.getReservationPolicy().getMaxReservationHours(), room.getReservationPolicy().getAvailableDays(),
                            timeSlots, collegeRestrictionPolicies, departmentRestrictionPolicies);
                    return new RoomDto.RoomResponse(room.getBuilding().getId(), room.getBuilding().getName(),
                            room.getId(), room.getName(), room.getDescription(), room.isAvailable(), reservationPolicy);
                }).collect(Collectors.toList());
    }

    public Long definePolicy(ReservationPolicy policy, Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        room.definePolicy(policy);
        return room.getId();
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class roomResponse {
        private String status;
        private String message;
    }
}
