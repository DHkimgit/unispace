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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class University extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "UNIVERSITY_ID")
    private Long id;
    private String name;
    private String address;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "university", cascade = CascadeType.PERSIST, orphanRemoval = true)
//    private List<College> colleges = new ArrayList<>();
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "university", cascade = CascadeType.PERSIST, orphanRemoval = true)
//    private List<Department> departments = new ArrayList<>();
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "university")
//    private List<User> userList = new ArrayList<>();

    private University(String name, String address) {
        this.name = name;
        this.address = address;
    }

    //==생성 메서드==//
    public static University createUniversity(String name, String address){
        return new University(name, address);
    }

    //==연관관계 편의 메서드==//
//    public void addCollege(College college) {
//        colleges.add(college);
//    }
//
//    public void addUniversityDepartment(Department department) {departments.add(department);}
//
//    public void removeCollege(College college) {
//        colleges.remove(college);
//    }
//
//    public void addUser(User user) {
//        this.userList.add(user);
//        if (user.getUniversity() != this) {
//            user.changeUniversity(this);
//        }
//    }

}
