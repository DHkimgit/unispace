//package com.project.unispace.domain.user;
//
//import com.project.unispace.domain.university.entity.College;
//import com.project.unispace.domain.university.entity.Department;
//import com.project.unispace.domain.university.entity.DepartmentAffiliationType;
//import com.project.unispace.domain.university.entity.University;
//import com.project.unispace.domain.university.repository.CollegeRepository;
//import com.project.unispace.domain.university.repository.DepartmentRepository;
//import com.project.unispace.domain.university.repository.UniversityRepository;
//import com.project.unispace.domain.user.dto.UserDto;
//import com.project.unispace.domain.user.entity.User;
//import com.project.unispace.domain.user.entity.UserRole;
//import com.project.unispace.domain.user.service.UserService;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Transactional
//public class userServiceTest {
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private UniversityRepository universityRepository;
//    @Autowired
//    private DepartmentRepository departmentRepository;
//    @Autowired
//    private CollegeRepository collegeRepository;
//
//    private University universityA;
//    private College collegeA;
//    private Department departmentA;
//
//    private User userA;
//    private User userB;
//
//    private UserDto.CreateRequest createRequest;
//
//    @BeforeEach
//    void setUp(){
//        universityA = University.createUniversity("한국기술교육대학교", "천안시 동남구");
//        collegeA = College.createCollege(universityA, "전기전자정보통신공학부");
//        departmentA = Department.createCollegeDepartment("전자공학과", false, DepartmentAffiliationType.COLLEGE, collegeA);
//        collegeA.addDepartment(departmentA);
//        universityA.addCollege(collegeA);
//        collegeRepository.save(collegeA);
//        University university = universityRepository.save(universityA);
//
//        createRequest = new UserDto.CreateRequest("abcde@gmail.com", "1q2w3e4r",
//                "1q2w3e4r!", "202136017",
//                "홍길동", "의적",
//                "010-1234-5678", universityA.getId(),
//                collegeA.getId(), departmentA.getId());
//    }
//
//    @Test
//    @DisplayName("학부에 속한 학과를 가진 일반 사용자 회원가입")
//    void registerWithCollegeUser(){
//        UserDto.ResponseTokens responseTokens = userService.saveUserWithCollege(createRequest);
//        System.out.println("responseTokens = " + responseTokens.getUserId() + responseTokens.getJwt());
//        Assertions.assertThat(responseTokens.getUserId()).isEqualTo(1);
//    }
//
//}


//        userA = User.createUserWithCollege(
//                "202136017", "1q2w3e4r",
//                "1q2w3e4r!", "홍길동",
//                "의적", "010-1234-5678",
//                "abcde@gmail.com", 0,
//                true, universityA,
//                collegeA, departmentA, UserRole.USER
//        );
//
//        userB = User.createUserWithCollege(
//                "202358965", "1q2w3e44",
//                "1q2w3e4r!", "김철수",
//                "의적", "010-5432-5678",
//                "qwerty@gmail.com", 0,
//                true, universityA,
//                collegeA, departmentA, UserRole.USER
//        );
