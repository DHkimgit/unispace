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
public class College extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "COLLEGE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIVERSITY_ID")
    private University university;

    @Column(name = "COLLEGE_NAME")
    private String name;

//    @OneToMany(mappedBy = "college", cascade = CascadeType.PERSIST, orphanRemoval = true)
//    private List<Department> departments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "college")
//    private List<User> userList = new ArrayList<>();

    private College(University university, String name) {
        this.university = university;
        this.name = name;
    }

    //==생성 메서드==//
    public static College createCollege(University university, String name){
        return new College(university, name);
    }

    //==연관관계 편의 메서드==//
//    public void addUser(User user) {
//        this.userList.add(user);
//        if (user.getCollege() != this) {
//            user.changeCollege(this);
//        }
//    }
//
//    public void addDepartment(Department department){
//        departments.add(department);
//    }

    //==비즈니스 로직==//


}
