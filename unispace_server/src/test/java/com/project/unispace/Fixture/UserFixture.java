package com.project.unispace.Fixture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.university.entity.DepartmentAffiliationType;
import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.university.repository.CollegeRepository;
import com.project.unispace.domain.university.repository.DepartmentRepository;
import com.project.unispace.domain.university.repository.UniversityRepository;
import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.entity.UserRole;
import com.project.unispace.domain.user.repository.UserRepository;
import com.project.unispace.domain.user.service.JwtService;

@Component
public final class UserFixture {

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;

    private final JwtService jwtService;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserFixture(
        UserRepository userRepository,
        UniversityRepository universityRepository,
        CollegeRepository collegeRepository,
        DepartmentRepository departmentRepository,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.universityRepository = universityRepository;
        this.collegeRepository = collegeRepository;
        this.departmentRepository = departmentRepository;
        this.jwtService = jwtService;
    }

    public User userA(University university, College college, Department department) {
        // University university = University.createUniversity("낙성대학교", "서울시 관악구");
        // universityRepository.save(university);
        //
        // College college = College.createCollege(university, "디저트학부");
        // collegeRepository.save(college);
        //
        // Department department = Department.createCollegeDepartment("탕후루과", false, DepartmentAffiliationType.COLLEGE, college);
        // departmentRepository.save(department);

        String encodedPassword = passwordEncoder.encode("1q2w3e4r!");

        return userRepository.save(
            User.createUserWithCollege(
                "2021136001",
                "usera",
                encodedPassword,
                "김두현",
                "김두현",
                "010-9139-2374",
                "usera@gmail.com",
                0,
                true,
                university,
                college,
                department,
                UserRole.USER
            )
        );
    }

    public String getToken(User user) {
        return jwtService.generateToken(new UserDetailsImpl(user, "usera", "1q2w3e4r!"));
    }
}
