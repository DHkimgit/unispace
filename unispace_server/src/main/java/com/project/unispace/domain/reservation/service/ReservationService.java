package com.project.unispace.domain.reservation.service;

import com.project.unispace.domain.reservation.dto.ReservationDto;
import com.project.unispace.domain.reservation.dto.ReservationDto.AvailableRoom;
import com.project.unispace.domain.reservation.dto.ReservationDto.reservationFriend;
import com.project.unispace.domain.reservation.dto.ReservationDto.reservationRequest;
import com.project.unispace.domain.reservation.dto.ReservationDto.reservationResponse;
import com.project.unispace.domain.reservation.entity.*;
import com.project.unispace.domain.reservation.repository.*;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.repository.UserRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationFriendRepository friendRepository;
    private final RoomRepository roomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final ReservationPolicyRepository policyRepository;

    public reservationResponse makeReservation(reservationRequest request, User reserveUser) {
        Room reserveRoom = roomRepository.findById(request.getRoomId()).orElseThrow();
        ReservationTimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId()).orElseThrow();
        Reservation newReservation = Reservation.createReservation(reserveUser, reserveRoom, request.getReserveDate(), timeSlot, request.getDescription());
        ReservationPolicy roomPolicy = reserveRoom.getReservationPolicy();

        if(!(request.getFriends() == null)) {
            request.getFriends()
                    .forEach(friendId -> {
                        User friend = userRepository.findById(friendId).orElseThrow();
                        addFriend(roomPolicy, newReservation, friend);
                    });
        }
        Reservation saved = reservationRepository.save(newReservation);

        System.out.println("reservation = " + saved);

        if(!saved.getReservationFriends().isEmpty()){
            Set<reservationFriend> friends = saved.getReservationFriends().stream()
                .map(reservationFriend -> {
                    return new reservationFriend(reservationFriend.getFriend().getId(), reservationFriend.getFriend().getNickname());
                }).collect(Collectors.toSet());
            return reservationResponse.builder()
                    .reserveDate(saved.getReservationDate())
                    .userId(saved.getReservedBy().getId())
                    .friends(friends)
                    .timeSlotId(saved.getTimeSlot().getId())
                    .startTime(saved.getTimeSlot().getStartTime())
                    .endTime(saved.getTimeSlot().getEndTime())
                    .roomId(saved.getRoom().getId())
                    .buildingName(saved.getRoom().getBuilding().getName())
                    .roomName(saved.getRoom().getName())
                    .description(request.getDescription()).build();
        }
        else{
            return reservationResponse.builder()
                    .reserveDate(saved.getReservationDate())
                    .userId(saved.getReservedBy().getId())
                    .friends(null)
                    .timeSlotId(saved.getTimeSlot().getId())
                    .startTime(saved.getTimeSlot().getStartTime())
                    .endTime(saved.getTimeSlot().getEndTime())
                    .roomId(saved.getRoom().getId())
                    .buildingName(saved.getRoom().getBuilding().getName())
                    .roomName(saved.getRoom().getName())
                    .description(request.getDescription()).build();
        }
    }

    //====오늘 날짜 ~ 예약 정책에서 정의된 예약 가능한 가장 미래의 날짜 사이에 방 예약이 존재하는지 확인====//
    public List<reservationResponse> checkExistReservation(Long roomId) {
        ReservationPolicy policy = policyRepository.findByRoomId(roomId);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(policy.getMinDaysBeforeReservation());

        return reservationRepository.findReservationsBetweenDates(roomId, startDate, endDate).stream()
                .map(reservation -> reservationResponse.builder()
                        .roomId(reservation.getRoom().getId())
                        .roomName(reservation.getRoom().getName())
                        .reserveDate(reservation.getReservationDate())
                        .startTime(reservation.getTimeSlot().getStartTime())
                        .endTime(reservation.getTimeSlot().getEndTime()).build())
                .collect(Collectors.toList());
    }

    // 예약 가능한 날짜와 시간대를 반환
    public List<AvailableRoom> getAvailableRoom(Long roomId) {
        ReservationPolicy policy = policyRepository.findByRoomId(roomId);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(policy.getMinDaysBeforeReservation());

        // 이미 예약된 정보들 확인 후 저장
        List<ExistReservationData> existReservationData = new ArrayList<>();
        List<Reservation> existReservations = reservationRepository.findReservationsBetweenDates(roomId, startDate, endDate);
        for(Reservation reservation : existReservations) {
            existReservationData.add(new ExistReservationData(reservation.getReservationDate(), reservation.getTimeSlot().getId()));
        }

        // timeslot 데이터 불러오기
        List<ReservationTimeSlot> timeSlotList = timeSlotRepository.getReservationTimeSlotsByReservationPolicyId(policy.getId());

        List<AvailableRoom> responses = new ArrayList<>();
        Period diff = Period.between(startDate, endDate);

        for(int i = 0; i < diff.getDays(); i++){
            LocalDate currentDate = startDate.plusDays(i);
            List<Long> cantReserveTimeSlotId = new ArrayList<>();
            AvailableRoom availableRoom = new AvailableRoom(roomId, currentDate);
            for(ExistReservationData e : existReservationData) {
                if(e.getReservationDate().equals(currentDate)) {
                    cantReserveTimeSlotId.add(e.getTimeSlotId());
                }
            }

            for(ReservationTimeSlot timeSlot : timeSlotList) {
                boolean flag = false;
                for(Long cantReserveTime : cantReserveTimeSlotId) {
                    if(timeSlot.getId().equals(cantReserveTime)) {
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    ReservationDto.AvailableTime availableTime = new ReservationDto.AvailableTime(timeSlot.getId(), timeSlot.getStartTime(), timeSlot.getEndTime());
                    availableRoom.addAvailableTime(availableTime);
                }
                else{
                    ReservationDto.UnavailableTime unavailableTime = new ReservationDto.UnavailableTime(timeSlot.getId(), timeSlot.getStartTime(), timeSlot.getEndTime());
                    availableRoom.addUnavailableTime(unavailableTime);
                }
            }
            responses.add(availableRoom);
        }
        return responses;
    }

    @Data
    @Getter @Setter
    @AllArgsConstructor
    public static class ExistReservationData {
        private LocalDate reservationDate;
        private Long timeSlotId;
    }

    // 사용자가 예약 가능한 방인지 확인
    // 단과대학 제약조건 확인
    public boolean checkCollegePolicyAvailableToUser(College userCollege, ReservationPolicy rp) {
        boolean collegeFlag = false;

        Set<CollegePolicy> collegePolicies = rp.getCollegePolicies();
        if(collegePolicies != null) {
            for (CollegePolicy collegePolicy : collegePolicies) {
                if (collegePolicy.getCollege().equals(userCollege)) {
                    collegeFlag = true;
                    break;
                }
            }
        }
        return collegeFlag;
    }
    // 학과 제약조건 확인
    public boolean checkDepartmentPolicyAvailableToUser(Department userDepartment, ReservationPolicy rp) {
        boolean departmentFlag = false;

        Set<DepartmentPolicy> departmentPolicies = rp.getDepartmentPolicies();
        if(departmentPolicies != null) {
            for(DepartmentPolicy departmentPolicy : departmentPolicies) {
                if(departmentPolicy.getDepartment().equals(userDepartment)) {
                    departmentFlag = true;
                    break;
                }
            }
        }

        return departmentFlag;
        // 학과 제약조건 확인
    }

    // 친구 추가 재약조건 확인
    public void addFriend(ReservationPolicy rp, Reservation reservation, User friend) {
        if(rp.isOpenToPublic()) {
            reservation.addFriend(friend);
        }
        else {
            System.out.println("쿼리 나가나?");
            if (rp.getRoom().getBuilding().getUniversity() == friend.getUniversity()){
                reservation.addFriend(friend);
            }
        }
    }

}
