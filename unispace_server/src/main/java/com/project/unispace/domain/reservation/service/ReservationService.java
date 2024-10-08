package com.project.unispace.domain.reservation.service;

import com.project.unispace.domain.reservation.dto.ReservationDto;
import com.project.unispace.domain.reservation.dto.ReservationDto.*;
import com.project.unispace.domain.reservation.dto.RoomDto;
import com.project.unispace.domain.reservation.entity.*;
import com.project.unispace.domain.reservation.repository.*;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.repository.UserRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.project.unispace.domain.reservation.dto.ReservationDto.*;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationFriendRepository friendRepository;
    private final RoomRepository roomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final ReservationPolicyRepository policyRepository;
    private final ReservationInquiryRepository reservationInquiryRepository;
    private final ReservationRedisService redisService;

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
            if(reservation.getStatus() == ReservationStatus.ACCEPTED || reservation.getStatus() == ReservationStatus.PENDING || reservation.getStatus() == ReservationStatus.COMPLETED) {
                existReservationData.add(new ExistReservationData(reservation.getReservationDate(), reservation.getTimeSlot().getId()));
            }
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
                    AvailableTime availableTime = new AvailableTime(timeSlot.getId(), timeSlot.getStartTime(), timeSlot.getEndTime());
                    availableRoom.addAvailableTime(availableTime);
                }
                else{
                    UnavailableTime unavailableTime = new UnavailableTime(timeSlot.getId(), timeSlot.getStartTime(), timeSlot.getEndTime());
                    availableRoom.addUnavailableTime(unavailableTime);
                }
            }
            responses.add(availableRoom);
        }
        return responses;
    }

    public RoomDto.RoomResponse getRoomInformation(Long roomId) {
        Room room = roomRepository.findRoomByIdWithFetchJoin(roomId);
        List<RoomDto.CollegeRestrictionPolicy> collegeRestrictionPolicies = new ArrayList<>();
        List<RoomDto.DepartmentRestrictionPolicy> departmentRestrictionPolicies = new ArrayList<>();
        if(room.getReservationPolicy().getCollegePolicies() != null){
            collegeRestrictionPolicies = room.getReservationPolicy().getCollegePolicies().stream()
                    .map(collegePolicy -> new RoomDto.CollegeRestrictionPolicy(collegePolicy.getCollege().getId(), collegePolicy.getCollege().getName())).toList();
        }

        if(room.getReservationPolicy().getDepartmentPolicies() != null) {
            departmentRestrictionPolicies = room.getReservationPolicy().getDepartmentPolicies().stream()
                    .map(departmentPolicy -> new RoomDto.DepartmentRestrictionPolicy(departmentPolicy.getDepartment().getId(), departmentPolicy.getDepartment().getName())).toList();
        }

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
            if (rp.getRoom().getBuilding().getUniversity() == friend.getUniversity()){
                reservation.addFriend(friend);
            }
        }
    }

    // 현재 날짜 이후의 사용자의 가장 최근의 예약 반환
    public LatestReservationResponse getClosestReservationResponse(Reservation reservation, Long userId) {
        return LatestReservationResponse.builder()
                .userId(userId)
                .timeSlotId(reservation.getTimeSlot().getId())
                .reserveDate(reservation.getReservationDate())
                .startTime(reservation.getTimeSlot().getStartTime())
                .endTime(reservation.getTimeSlot().getEndTime())
                .roomId(reservation.getRoom().getId())
                .buildingName(reservation.getRoom().getBuilding().getName())
                .roomName(reservation.getRoom().getName())
                .build();

    }

    public Reservation getClosestReservation(Long userId) {
        return reservationRepository.findClosestReservationAfterToday(userId);
    }

    // 현재 날짜, 시간 이후 사용자의 모든 예약 내역 반환
    public List<Reservation> getUpcomingReservations(Long userId) {
        return reservationRepository.findUpcomingReservationsByUserId(userId);
    }

    public List<ReservationResponses> getUpcomingReservationsResponse(List<Reservation> reservations, Long userId) {
        return reservations.stream()
                .map(reservation -> {
                    return ReservationResponses.builder()
                            .userId(userId)
                            .reservationId(reservation.getId())
                            .timeSlotId(reservation.getTimeSlot().getId())
                            .reserveDate(reservation.getReservationDate())
                            .startTime(reservation.getTimeSlot().getStartTime())
                            .endTime(reservation.getTimeSlot().getEndTime())
                            .roomId(reservation.getRoom().getId())
                            .buildingName(reservation.getRoom().getBuilding().getName())
                            .roomName(reservation.getRoom().getName())
                            .member(reservation.getReservationFriends().size())
                            .status(reservation.getStatus())
                            .build();
                }).collect(Collectors.toList());
    }

    // 사용자의 취소/이용 완료된 예약 내역 반환
    public List<Reservation> getCanceledOrCompletedReservations(Long userId) {
        return reservationRepository.findCanceledOrCompletedReservationsByUserId(userId);
    }

    public List<ReservationResponses> getCanceledOrCompletedReservationsResponse(List<Reservation> reservations, Long userId) {
        return reservations.stream()
                .map(reservation -> {
                    return ReservationResponses.builder()
                            .userId(userId)
                            .reservationId(reservation.getId())
                            .timeSlotId(reservation.getTimeSlot().getId())
                            .reserveDate(reservation.getReservationDate())
                            .startTime(reservation.getTimeSlot().getStartTime())
                            .endTime(reservation.getTimeSlot().getEndTime())
                            .roomId(reservation.getRoom().getId())
                            .buildingName(reservation.getRoom().getBuilding().getName())
                            .roomName(reservation.getRoom().getName())
                            .member(reservation.getReservationFriends().size())
                            .status(reservation.getStatus())
                            .build();
                }).collect(Collectors.toList());
    }

    // 사용자의 승인 대기중인 예약 내역 반환
    public List<Reservation> getPendingReservations(Long userId) {
        return reservationRepository.findPendingReservationsByUserId(userId);
    }

    public List<ReservationResponses> getPendingReservationsResponse(List<Reservation> reservations, Long userId) {
        return reservations.stream()
                .map(reservation -> {
                    return ReservationResponses.builder()
                            .userId(userId)
                            .reservationId(reservation.getId())
                            .timeSlotId(reservation.getTimeSlot().getId())
                            .reserveDate(reservation.getReservationDate())
                            .startTime(reservation.getTimeSlot().getStartTime())
                            .endTime(reservation.getTimeSlot().getEndTime())
                            .roomId(reservation.getRoom().getId())
                            .buildingName(reservation.getRoom().getBuilding().getName())
                            .roomName(reservation.getRoom().getName())
                            .member(reservation.getReservationFriends().size())
                            .status(reservation.getStatus())
                            .build();
                }).collect(Collectors.toList());
    }

    // 사용자의 거절된 예약 내역 반환
    public List<Reservation> getRejectedReservations(Long userId) {
        return reservationRepository.findRejectedReservationsByUserId(userId);
    }

    public List<ReservationResponses> getRejectedReservationsResponse(List<Reservation> reservations, Long userId) {
        return reservations.stream()
                .map(reservation -> {
                    return ReservationResponses.builder()
                            .userId(userId)
                            .reservationId(reservation.getId())
                            .timeSlotId(reservation.getTimeSlot().getId())
                            .reserveDate(reservation.getReservationDate())
                            .startTime(reservation.getTimeSlot().getStartTime())
                            .endTime(reservation.getTimeSlot().getEndTime())
                            .roomId(reservation.getRoom().getId())
                            .buildingName(reservation.getRoom().getBuilding().getName())
                            .roomName(reservation.getRoom().getName())
                            .member(reservation.getReservationFriends().size())
                            .status(reservation.getStatus())
                            .build();
                }).collect(Collectors.toList());
    }



    /*
    * 예약 승인
    * */
    @Transactional
    public void acceptReservation(Long reservationId, Long adminId, String message) {
        Reservation reservation = reservationRepository.getReservationById(reservationId);
        reservation.acceptReservation(adminId, message);
    }

    /*
     * 예약 거절
     * */
    @Transactional
    public void rejectReservation(Long reservationId, Long adminId, String message) {
        Reservation reservation = reservationRepository.getReservationById(reservationId);
        reservation.rejectReservation(adminId, message);
    }

    /*
     * 예약 취소
     * */
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.getReservationById(reservationId);
        reservation.cancelReservation();
    }

    /*
    * 문의 생성
    * */
    @Transactional
    public InquiryCreateResponse createInquiry(InquiryCreateRequest request, User user) {
        Reservation reservation = reservationRepository.findById(request.getReservationId()).orElseThrow();
        ReservationInquiry inquiry = ReservationInquiry.CreateInquiry(reservation, user, request.getContents());
        ReservationInquiry save = reservationInquiryRepository.save(inquiry);

        return InquiryCreateResponse.builder()
                .inquiryId(save.getId())
                .userId(user.getId())
                .reservationId(reservation.getId())
                .contents(save.getContent())
                .status(save.getStatus())
                .build();
    }

    /*
    * 상세 예약 정보 반환
    * */
    public reservationResponse specificReservationData(Long reservationId) {
        Reservation saved = reservationRepository.getReservationById(reservationId);

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
                    .description(saved.getDescription()).build();
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
                    .description(saved.getDescription()).build();
        }
    }

    public boolean lockTimeSlot(Long roomId, LocalDate reservationDate, Long timeSlotId, Long userId) {
        return redisService.lockTimeSlot(
                roomId.toString(),
                reservationDate.toString(),
                timeSlotId.toString(),
                userId.toString(),
                300 // 5분 동안 락 유지
        );
    }

    public void unlockTimeSlot(Long roomId, LocalDate reservationDate, Long timeSlotId) {
        redisService.unlockTimeSlot(
                roomId.toString(),
                reservationDate.toString(),
                timeSlotId.toString()
        );
    }

    public boolean isTimeSlotLocked(Long roomId, LocalDate reservationDate, Long timeSlotId) {
        return redisService.getTimeSlotLockOwner(
                roomId.toString(),
                reservationDate.toString(),
                timeSlotId.toString()
        ) != null;
    }

    @Transactional
    public ReservationDto.reservationResponse makeReservationWithLock(ReservationDto.reservationRequest request, User reserveUser) {
        String lockKey = String.format("%d:%s:%d", request.getRoomId(), request.getReserveDate(), request.getTimeSlotId());
//        if (!redisService.getTimeSlotLockOwner(request.getRoomId().toString(), request.getReserveDate().toString(), request.getTimeSlotId().toString())
//                .equals(reserveUser.getId().toString())) {
//            throw new IllegalStateException("예약 선점 권한이 없습니다.");
//        }

        try {
            ReservationDto.reservationResponse response = makeReservation(request, reserveUser);
            unlockTimeSlot(request.getRoomId(), request.getReserveDate(), request.getTimeSlotId());
            return response;
        } catch (Exception e) {
            unlockTimeSlot(request.getRoomId(), request.getReserveDate(), request.getTimeSlotId());
            throw e;
        }
    }

    public boolean renewTimeSlotLock(Long roomId, LocalDate reservationDate, Long timeSlotId, Long userId) {
        return redisService.renewLock(
                roomId.toString(),
                reservationDate.toString(),
                timeSlotId.toString(),
                userId.toString(),
                300 // 5분 연장
        );
    }

}
