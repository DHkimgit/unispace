package com.project.unispace.domain.university.entity;
import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Department extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "DEPARTMENT_ID")
    private Long id;

    @Column(name = "DEPARTMENT_NAME")
    private String name;

    private boolean isGraduateSchool;

    @Enumerated(EnumType.STRING)
    private DepartmentAffiliationType affiliation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIVERSITY_ID")
    private University university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLEGE_ID")
    private College college;

//    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
//    private List<User> userList = new ArrayList<>();


    private Department(String name, boolean isGraduateSchool, DepartmentAffiliationType affiliation, University university) {
        this.name = name;
        this.isGraduateSchool = isGraduateSchool;
        this.affiliation = affiliation;
        this.university = university;
    }

    private Department(String name, boolean isGraduateSchool, DepartmentAffiliationType affiliation, College college) {
        this.name = name;
        this.isGraduateSchool = isGraduateSchool;
        this.affiliation = affiliation;
        this.college = college;
    }

    //===생성 메서드===//
    public static Department createUniversityDepartment(String name, boolean isGraduateSchool, DepartmentAffiliationType affiliation, University university){
        return new Department(name, isGraduateSchool, DepartmentAffiliationType.UNIVERSITY, university);
    }

    public static Department createCollegeDepartment(String name, boolean isGraduateSchool, DepartmentAffiliationType affiliation, College college){
        return new Department(name, isGraduateSchool, DepartmentAffiliationType.COLLEGE, college);
    }

    //===연관관계 편의 메서드===//
//    public void addUser(User user) {
//        this.userList.add(user);
//        if (user.getDepartment() != this) {
//            user.changeDepartment(this);
//        }
//    }

    //===비즈니스 로직===//
    public void changeCollege(College college){
        this.college = college;
    }

    public void changeAffiliationTypeToUniversity(University university){
        this.affiliation = DepartmentAffiliationType.UNIVERSITY;
        this.college = null;
        this.university = university;
    }

    public void changeAffiliationTypeToCollege(College college){
        this.affiliation = DepartmentAffiliationType.COLLEGE;
        this.college = college;
        this.university = null;
    }

}
