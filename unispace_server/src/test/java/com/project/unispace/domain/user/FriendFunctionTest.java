package com.project.unispace.domain.user;

import com.project.unispace.commons.exception.AlreadyExistsException;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.university.entity.DepartmentAffiliationType;
import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.university.repository.CollegeRepository;
import com.project.unispace.domain.university.repository.DepartmentRepository;
import com.project.unispace.domain.university.repository.UniversityRepository;

import com.project.unispace.domain.user.dto.FriendDto;
import com.project.unispace.domain.user.entity.Friend;
import com.project.unispace.domain.user.entity.FriendStatus;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.entity.UserRole;
import com.project.unispace.domain.user.repository.FriendRepository;
import com.project.unispace.domain.user.repository.UserRepository;
import com.project.unispace.domain.user.service.FriendService;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class FriendFunctionTest {
    @Autowired
    private FriendService friendService;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UniversityRepository universityRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private CollegeRepository collegeRepository;
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private University university1;
    private College college1;
    private Department department1;

    @BeforeEach
    void setUp(){
        university1 = University.createUniversity("한국기술교육대학교", "천안시 동남구");
        college1 = College.createCollege(university1, "전기전자정보통신공학부");
        universityRepository.save(university1);
        collegeRepository.save(college1);
        department1 = Department.createCollegeDepartment("전자공학과", false, DepartmentAffiliationType.COLLEGE, college1);
        departmentRepository.save(department1);
        user1 = User.createUserWithCollege("21-139456", "userA", "1q2w3e4r!",
                "홍길동", "홍길동", "010-9139-2384",
                "userA@naver.com", 0, true,
                university1, college1, department1, UserRole.USER
        );
        user2 = User.createUserWithCollege("21-189415", "userB", "1q2w3e4r!",
                "김철수", "김철수", "010-1634-2374",
                "userB@naver.com", 0, true,
                university1, college1, department1, UserRole.USER
        );
        user3 = User.createUserWithCollege("21-184245", "userC", "1q2w3e4r!",
                "김영희", "김영희", "010-1247-2374",
                "userC@naver.com", 0, true,
                university1, college1, department1, UserRole.USER
        );
        user4 = User.createUserWithCollege("20-778285", "userD", "1q2w3e4r!",
                "신짱구", "짱구", "010-1974-5698",
                "userD@naver.com", 0, true,
                university1, college1, department1, UserRole.USER
        );
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
    }

    @Test
    @DisplayName("친구 추가 엔티티 생성 테스트 - user1이 user2에게로 요청, 같은 대학")
    void createSameUnivFriendRequestCreateTest(){
        User user1 = userRepository.findById(this.user1.getId()).orElseThrow(() -> new EntityNotFoundException("noSuchIdUser"));
        User user2 = userRepository.findById(this.user2.getId()).orElseThrow(() -> new EntityNotFoundException("noSuchIdUser"));
        User user3 = userRepository.findById(this.user3.getId()).orElseThrow(() -> new EntityNotFoundException("noSuchIdUser"));
        try{
            friendService.createFriendRequest(user2.getId(), user1.getId());
            friendService.createFriendRequest(user3.getId(), user1.getId());
        } catch (AlreadyExistsException e){
            System.out.println("히");
        }

//        List<Friend> list = friendRepository.findAll().stream().toList();
//        for (Friend friend : list) {
//            System.out.println("friend.toString() = " + friend.getId());
//            System.out.println("friend.getRequestUser().getUsername()" + friend.getRequestUser().getUsername());
//            System.out.println("friend.getReceiveUser().getUsername()" + friend.getReceiveUser().getUsername());
//        }
//        List<FriendDto.FriendPair> friendPairs = friendRepository.findFriendPairsByUserId(user1.getId());
        List<Friend> allFriends = friendRepository.findAllFriendsByUserId(user1.getId());
        Map<Long, Friend> friendMap = new HashMap<>();
        List<FriendDto.FriendPair> friendPairs = new ArrayList<>();

        for (Friend friend : allFriends) {
            Long otherUserId = friend.getRequestUser().getId().equals(user1.getId())
                    ? friend.getReceiveUser().getId()
                    : friend.getRequestUser().getId();

            if (friendMap.containsKey(otherUserId)) {
                friendPairs.add(new FriendDto.FriendPair(friendMap.get(otherUserId), friend));
                friendMap.remove(otherUserId);
            } else {
                friendMap.put(otherUserId, friend);
            }
        }


        for (FriendDto.FriendPair pair : friendPairs) {
            Friend friend1 = pair.getFriend1();
            Friend friend2 = pair.getFriend2();
            System.out.println("Pair: " +
                    "<request_user_id: " + friend1.getRequestUser().getId() +
                    ", receive_user_id: " + friend1.getReceiveUser().getId() + ">, " +
                    "<request_user_id: " + friend2.getRequestUser().getId() +
                    ", receive_user_id: " + friend2.getReceiveUser().getId() + ">");
        }

    }

    @Test
    @DisplayName("친구 추가 요청 시 예외 발생 테스트 - 같은 요청이 이미 존재할 경우")
    void testCreateFriendRequestThrowsException() {
        // 유저 초기화
        User user1 = userRepository.findById(this.user1.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user2 = userRepository.findById(this.user2.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 첫 번째 요청은 성공적으로 수행되어야 함
        friendService.createFriendRequest(user2.getId(), user1.getId());

        // 동일한 요청을 다시 수행했을 때 AlreadyExistsException 예외가 발생해야 테스트가 성공함
        assertThrows(AlreadyExistsException.class, () -> {
            friendService.createFriendRequest(user2.getId(), user1.getId());
        });
    }

    @Test
    @DisplayName("친구 요청시 예외 발생 테스트 - 이미 상대방이 나에게 요청을 보낸 상황인 경우")
    void 친구_요청시_예외_발생_테스트_이미_상대방이_나에게_요청을_보낸_상황인_경우(){
        User user1 = userRepository.findById(this.user1.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user2 = userRepository.findById(this.user2.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        friendService.createFriendRequest(user1.getId(), user2.getId());

        List<Friend> existRequest = friendRepository.findExistFriendRequest(user2.getId(), user1.getId());

        if (!existRequest.isEmpty()){
            for (Friend friend : existRequest) {
                System.out.println("friend.getRequestUser() = " + friend.getRequestUser().getLoginId());
                System.out.println("friend.getReceiveUser() = " + friend.getReceiveUser().getLoginId());
                System.out.println("friend.getStatus() = " + friend.getStatus());
            }
        }
    }

    @Test
    @DisplayName("친구 요청 시 예외 발생 테스트 - 이미 상대방이 내 요청을 거절한 상태인 경우")
    void testCreateFriendRequestToAlreadyRejectRequestUser() {
        User user1 = userRepository.findById(this.user1.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user2 = userRepository.findById(this.user2.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        friendService.createFriendRequest(user1.getId(), user2.getId());
        friendService.rejectFriendRequest(user1.getId(), user2.getId());

        // 5일이 경과되지 않은 경우 요청을 보낼 수 없음
        assertThrows(AlreadyExistsException.class, () -> {
            friendService.createFriendRequest(user1.getId(), user2.getId());
        });

        // 5일이 경과된 경우
        friendRepository.findAllFriendsByUserId(user2.getId()).stream()
                .filter(friend -> friend.getRequestUser().getId().equals(user2.getId()))
                .forEach(Friend::resendRequest);

        List<Friend> findRecord = friendRepository.findAllFriendsByUserId(user2.getId());
        for (Friend friend : findRecord) {
            if (Objects.equals(friend.getRequestUser().getId(), user2.getId())) {
                Assertions.assertThat(friend.getStatus()).isEqualTo(FriendStatus.PENDING);
            } else {
                Assertions.assertThat(friend.getStatus()).isEqualTo(FriendStatus.REJECTED);
            }
        }
    }

    @Test
    @DisplayName("받은 요청 조회 테스트")
    void testGetReceiveFriendRequestList(){
        // 사용자 초기화
        User user1 = userRepository.findById(this.user1.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user2 = userRepository.findById(this.user2.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user3 = userRepository.findById(this.user3.getId()).orElseThrow(() -> new EntityNotFoundException("noSuchIdUser"));
        User user4 = userRepository.findById(this.user4.getId()).orElseThrow(() -> new EntityNotFoundException("noSuchIdUser"));

        // 친구 요청 생성 - user2, 3, 4가 user1에게 친구 요청 전송
        friendService.createFriendRequest(user1.getId(), user2.getId());
        friendService.createFriendRequest(user1.getId(), user3.getId());
        friendService.createFriendRequest(user1.getId(), user4.getId());
        System.out.println("아무것도 안떠?");

    }
}
