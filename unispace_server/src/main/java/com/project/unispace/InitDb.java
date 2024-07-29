package com.project.unispace;

import com.project.unispace.domain.reservation.entity.*;
import com.project.unispace.domain.reservation.repository.BuildingRepository;
import com.project.unispace.domain.reservation.repository.ReservationPolicyRepository;
import com.project.unispace.domain.reservation.repository.RoomRepository;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.university.entity.DepartmentAffiliationType;
import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.university.repository.CollegeRepository;
import com.project.unispace.domain.university.repository.DepartmentRepository;
import com.project.unispace.domain.university.repository.UniversityRepository;
import com.project.unispace.domain.user.dto.FriendDto;
import com.project.unispace.domain.user.entity.Friend;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.entity.UserRole;
import com.project.unispace.domain.user.repository.FriendRepository;
import com.project.unispace.domain.user.repository.UserRepository;
import com.project.unispace.domain.user.service.FriendService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.initAdmin();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final UniversityRepository universityRepository;
        private final DepartmentRepository departmentRepository;
        private final CollegeRepository collegeRepository;
        private final FriendRepository friendRepository;
        private final BuildingRepository buildingRepository;
        private final ReservationPolicyRepository reservationPolicyRepository;
        private final RoomRepository roomRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final FriendService friendService;

        public void dbInit1(){
            University university1 = University.createUniversity("한국기술교육대학교", "천안시 동남구");
            universityRepository.save(university1);
            University university2 = University.createUniversity("서울대학교", "서울시 관악구");
            universityRepository.save(university2);
            College college1 = College.createCollege(university1, "전기전자정보통신공학부");
            collegeRepository.save(college1);
            College college2 = College.createCollege(university1, "에너지신소재공학부");
            collegeRepository.save(college2);
            Department department1 = Department.createCollegeDepartment("전자공학과", false, DepartmentAffiliationType.COLLEGE, college1);
            departmentRepository.save(department1);
            Department department2 = Department.createCollegeDepartment("전기공학과", false, DepartmentAffiliationType.COLLEGE, college1);
            departmentRepository.save(department2);
            Department department3 = Department.createCollegeDepartment("정보통신공학과", false, DepartmentAffiliationType.COLLEGE, college1);
            departmentRepository.save(department3);
            Department department4 = Department.createCollegeDepartment("에너지공학과", false, DepartmentAffiliationType.COLLEGE, college2);
            departmentRepository.save(department4);
            Department department5 = Department.createCollegeDepartment("신소재공학과", false, DepartmentAffiliationType.COLLEGE, college2);
            departmentRepository.save(department5);
            universityRepository.save(university1);
            String encodedPassword = passwordEncoder.encode("1q2w3e4r!");
            User user1 = User.createUserWithCollege("21-789456", "user1", encodedPassword,
                    "홍길동", "홍길동", "010-9139-2374",
                    "user1@naver.com", 0, true,
                    university1, college1, department1, UserRole.USER
            );
            User user2 = User.createUserWithCollege("21-789445", "user2", encodedPassword,
                    "김철수", "김철수", "010-1234-2374",
                    "user2@naver.com", 0, true,
                    university1, college1, department1, UserRole.USER
            );
            User user3 = User.createUserWithCollege("21-784245", "user3", encodedPassword,
                    "김영희", "김영희", "010-1274-2374",
                    "user3@naver.com", 0, true,
                    university1, college1, department1, UserRole.USER
            );
            User user4 = User.createUserWithCollege("20-778235", "user4", encodedPassword,
                    "신짱구", "짱구", "010-1984-5698",
                    "user4@naver.com", 0, true,
                    university1, college1, department1, UserRole.USER
            );
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);
            friendRepository.saveAll(Friend.createFriendRequest(user2, user1, true));
            friendRepository.saveAll(Friend.createFriendRequest(user3, user1, true));
            friendRepository.saveAll(Friend.createFriendRequest(user4, user1, true));
            Building buildingA = Building.createBuilding("인문경영관", "본관 건너편",
                    6, 2,
                    LocalTime.of(6, 0), LocalTime.of(23, 0));
            Building buildingB = Building.createBuilding("담헌실학관", "정문 건너편",
                    9, 2,
                    LocalTime.of(6, 0), LocalTime.of(23, 0));
            Set<String> request = new HashSet<>();
            request.add("MONDAY");
            request.add("TUESDAY");
            request.add("WEDNESDAY");
            request.add("THURSDAY");
            request.add("FRIDAY");
            Set<DayOfWeek> openDays = request.stream()
                    .map(DayOfWeek::valueOf)
                    .collect(Collectors.toSet());

            buildingA.setOpenDaysPolicy(openDays);
            buildingA.changeUniversity(university1);
            buildingB.setOpenDaysPolicy(openDays);
            buildingB.changeUniversity(university1);

            buildingRepository.save(buildingA);
            buildingRepository.save(buildingB);

            Room roomA1 = Room.createRoom("101호", "강의실", buildingA);
            Room roomA2 = Room.createRoom("102호", "강의실", buildingA);
            Room roomB1 = Room.createRoom("101호", "강의실", buildingB);
            Room roomB2 = Room.createRoom("102호", "강의실", buildingB);
            roomRepository.save(roomA1);
            roomRepository.save(roomB1);
            roomRepository.save(roomA2);
            roomRepository.save(roomB2);

            ReservationPolicy policyA = ReservationPolicy.createPolicy(roomA1, true, false, 1, LocalTime.of(8, 0, 0), LocalTime.of(20, 0, 0), 3);
            CollegePolicy cpA = CollegePolicy.createPolicy(policyA, college1);
            Set<CollegePolicy> collegePolicies = new HashSet<>();
            collegePolicies.add(cpA);
            policyA.setCollegePolicies(collegePolicies);
            DepartmentPolicy dpA = DepartmentPolicy.createPolicy(policyA, department1);
            Set<DepartmentPolicy> departmentPolicies = new HashSet<>();
            departmentPolicies.add(dpA);
            policyA.setDepartmentPolicies(departmentPolicies);
            Set<DayOfWeek> availableDays = new HashSet<>();
            availableDays.add(DayOfWeek.MONDAY);
            availableDays.add(DayOfWeek.TUESDAY);
            availableDays.add(DayOfWeek.WEDNESDAY);
            availableDays.add(DayOfWeek.THURSDAY);
            availableDays.add(DayOfWeek.FRIDAY);
            policyA.addAvailableDayPolicy(availableDays);
            policyA.generateTimeSlot();
            reservationPolicyRepository.save(policyA);

            ReservationPolicy policyA2 = ReservationPolicy.createPolicy(roomA2, true, false, 1, LocalTime.of(8, 0, 0), LocalTime.of(20, 0, 0), 3);
            CollegePolicy cpA2 = CollegePolicy.createPolicy(policyA2, college1);
            Set<CollegePolicy> collegePolicies2 = new HashSet<>();
            collegePolicies2.add(cpA2);
            policyA2.setCollegePolicies(collegePolicies2);
            DepartmentPolicy dpA2 = DepartmentPolicy.createPolicy(policyA2, department1);
            Set<DepartmentPolicy> departmentPolicies2 = new HashSet<>();
            departmentPolicies.add(dpA2);
            policyA2.setDepartmentPolicies(departmentPolicies2);
            Set<DayOfWeek> availableDays2 = new HashSet<>();
            availableDays2.add(DayOfWeek.MONDAY);
            availableDays2.add(DayOfWeek.TUESDAY);
            availableDays2.add(DayOfWeek.WEDNESDAY);
            availableDays2.add(DayOfWeek.THURSDAY);
            availableDays2.add(DayOfWeek.FRIDAY);
            policyA2.addAvailableDayPolicy(availableDays2);
            policyA2.generateTimeSlot();
            reservationPolicyRepository.save(policyA2);

            ReservationPolicy policyB = ReservationPolicy.createPolicy(roomB1, true, false, 1, LocalTime.of(8, 0, 0), LocalTime.of(20, 0, 0), 3);
            CollegePolicy cpB = CollegePolicy.createPolicy(policyB, college1);
            Set<CollegePolicy> collegePoliciesB = new HashSet<>();
            collegePoliciesB.add(cpB);
            policyB.setCollegePolicies(collegePoliciesB);
            DepartmentPolicy dpB = DepartmentPolicy.createPolicy(policyB, department1);
            Set<DepartmentPolicy> departmentPoliciesB = new HashSet<>();
            departmentPoliciesB.add(dpB);
            policyB.setDepartmentPolicies(departmentPoliciesB);
            Set<DayOfWeek> availableDaysB = new HashSet<>();
            availableDaysB.add(DayOfWeek.MONDAY);
            availableDaysB.add(DayOfWeek.TUESDAY);
            availableDaysB.add(DayOfWeek.WEDNESDAY);
            availableDaysB.add(DayOfWeek.THURSDAY);
            availableDaysB.add(DayOfWeek.FRIDAY);
            policyB.addAvailableDayPolicy(availableDaysB);
            policyB.generateTimeSlot();
            reservationPolicyRepository.save(policyB);

            ReservationPolicy policyB2 = ReservationPolicy.createPolicy(roomB2, true, false, 1, LocalTime.of(8, 0, 0), LocalTime.of(20, 0, 0), 3);
            CollegePolicy cpB2 = CollegePolicy.createPolicy(policyB2, college1);
            Set<CollegePolicy> collegePoliciesB2 = new HashSet<>();
            collegePoliciesB2.add(cpB2);
            policyB2.setCollegePolicies(collegePoliciesB2);
            DepartmentPolicy dpB2 = DepartmentPolicy.createPolicy(policyB2, department1);
            Set<DepartmentPolicy> departmentPoliciesB2 = new HashSet<>();
            departmentPoliciesB.add(dpB2);
            policyB2.setDepartmentPolicies(departmentPoliciesB2);
            Set<DayOfWeek> availableDaysB2 = new HashSet<>();
            availableDaysB2.add(DayOfWeek.MONDAY);
            availableDaysB2.add(DayOfWeek.TUESDAY);
            availableDaysB2.add(DayOfWeek.WEDNESDAY);
            availableDaysB2.add(DayOfWeek.THURSDAY);
            availableDaysB2.add(DayOfWeek.FRIDAY);
            policyB2.addAvailableDayPolicy(availableDaysB2);
            policyB2.generateTimeSlot();
            reservationPolicyRepository.save(policyB2);

            Room roomA3 = Room.createRoom("103호", "강의실", buildingA);
            Room roomA4 = Room.createRoom("104호", "강의실", buildingA);

            roomRepository.save(roomA3);
            roomRepository.save(roomA4);

            ReservationPolicy policyA3 = ReservationPolicy.createPolicy(roomA3, true, true, 2, LocalTime.of(8, 0, 0), LocalTime.of(20, 0, 0), 3);
            CollegePolicy cpA3_1 = CollegePolicy.createPolicy(policyA3, college1);
            CollegePolicy cpA3_2 = CollegePolicy.createPolicy(policyA3, college2);

            Set<CollegePolicy> collegePoliciesA3 = new HashSet<>();
            collegePoliciesA3.add(cpA3_1);
            collegePoliciesA3.add(cpA3_2);
            policyA3.setCollegePolicies(collegePoliciesA3);

            Set<DayOfWeek> availableDaysA3 = new HashSet<>();
            availableDaysA3.add(DayOfWeek.MONDAY);
            availableDaysA3.add(DayOfWeek.TUESDAY);
            availableDaysA3.add(DayOfWeek.WEDNESDAY);
            availableDaysA3.add(DayOfWeek.THURSDAY);
            availableDaysA3.add(DayOfWeek.FRIDAY);

            policyA3.addAvailableDayPolicy(availableDaysA3);

            policyA3.generateTimeSlot();

            reservationPolicyRepository.save(policyA3);

            // Creating new reservation policies for roomA4
            ReservationPolicy policyA4 = ReservationPolicy.createPolicy(roomA4, true, false, 2, LocalTime.of(8, 0, 0), LocalTime.of(22, 0, 0), 3);

            policyA4.addAvailableDayPolicy(availableDaysA3);

            policyA4.generateTimeSlot();

            reservationPolicyRepository.save(policyA4);

            Room roomB3 = Room.createRoom("360호", "강의실", buildingB);
            Room roomB4 = Room.createRoom("대회의실", "회의실", buildingB);

            roomRepository.save(roomB3);
            roomRepository.save(roomB4);

            ReservationPolicy policyB3 = ReservationPolicy.createPolicy(roomB3, true, false, 2, LocalTime.of(8, 0, 0), LocalTime.of(20, 0, 0), 3);
            CollegePolicy cpB3_1 = CollegePolicy.createPolicy(policyB3, college2);

            Set<CollegePolicy> collegePoliciesB3 = new HashSet<>();
            collegePoliciesB3.add(cpB3_1);
            policyB3.setCollegePolicies(collegePoliciesB3);

            Set<DayOfWeek> availableDaysB3 = new HashSet<>();
            availableDaysA3.add(DayOfWeek.MONDAY);
            availableDaysA3.add(DayOfWeek.TUESDAY);
            availableDaysA3.add(DayOfWeek.WEDNESDAY);
            availableDaysA3.add(DayOfWeek.THURSDAY);
            availableDaysA3.add(DayOfWeek.FRIDAY);

            policyB3.addAvailableDayPolicy(availableDaysB3);

            policyB3.generateTimeSlot();

            reservationPolicyRepository.save(policyB3);

            // Creating new reservation policies for roomB4
            ReservationPolicy policyB4 = ReservationPolicy.createPolicy(roomB4, true, false, 1, LocalTime.of(10, 0, 0), LocalTime.of(18, 0, 0), 3);

            policyB4.addAvailableDayPolicy(availableDaysB3);

            policyB4.generateTimeSlot();

            reservationPolicyRepository.save(policyB4);

        }


        public void initAdmin(){
            Optional<University> findUniversity = universityRepository.findById(1L);
            String encodedPassword = passwordEncoder.encode("1q2w3e4r!");
            User user = User.createAdmin("admin", encodedPassword, "관리자", "관리자","admin@unispace.com", true, findUniversity.get());
            userRepository.save(user);
        }
    }
}
