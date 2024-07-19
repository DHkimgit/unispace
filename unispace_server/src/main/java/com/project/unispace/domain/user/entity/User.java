package com.project.unispace.domain.user.entity;

import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.university.entity.University;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "USERS")
public class User extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;

    private String studentId;

    @NotEmpty
    @Column(unique = true)
    private String loginId;

    @NotEmpty
    private String password;

    private String username;

    private String nickname;

    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private int penaltyScore;

    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIVERSITY_ID")
    private University university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLEGE_ID")
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    private Department department;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private User(String studentId, String loginId, String password, String username, String nickname, String phoneNumber, String email, int penaltyScore, boolean enabled, University university, Department department, UserRole userRole) {
        this.studentId = studentId;
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.penaltyScore = penaltyScore;
        this.enabled = enabled;
        this.university = university;
        this.department = department;
        this.userRole = userRole;
    }

    private User(String studentId, String loginId, String password, String username, String nickname, String phoneNumber, String email, int penaltyScore, boolean enabled, University university, College college, Department department, UserRole userRole) {
        this.studentId = studentId;
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.penaltyScore = penaltyScore;
        this.enabled = enabled;
        this.university = university;
        this.college = college;
        this.department = department;
        this.userRole = userRole;
    }

    private User(String loginId, String password, String username, String nickname, String email, boolean enabled, University university, UserRole userRole){
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.enabled = enabled;
        this.university = university;
        this.userRole = userRole;
    }

    //===생성 메서드===//
    public static User createUserWithCollege(String studentId, String loginId, String password, String username, String nickname, String phoneNumber, String email, int penaltyScore, boolean enabled, University university, College college, Department department, UserRole userRole){
        return new User(studentId, loginId, password, username, nickname, phoneNumber, email, penaltyScore, enabled, university, college, department, userRole);
    }
    public static User createUserWithOutCollege(String studentId, String loginId, String password, String username, String nickname, String phoneNumber, String email, int penaltyScore, boolean enabled, University university, Department department, UserRole userRole){
        return new User(studentId, loginId, password, username, nickname, phoneNumber, email, penaltyScore, enabled, university, department, userRole);
    }

    public static User createAdmin(String loginId, String password, String username, String nickname, String email, boolean enabled, University university){
        return new User(loginId, password, username, nickname, email, enabled, university, UserRole.ADMIN);
    }

    //===수정 메서드===//
    public void changeDepartment(Department department){
        this.department = department;
    }

    public void changeUniversity(University university){
        this.university = university;
    }

    public void changeCollege(College college){
        this.college = college;
    }

}
