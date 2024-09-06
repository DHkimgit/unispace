package com.project.unispace.Fixture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.repository.CollegeRepository;

@Component
public class CollegeFixture {

    private final CollegeRepository collegeRepository;

    @Autowired
    public CollegeFixture(CollegeRepository collegeRepository) {
        this.collegeRepository = collegeRepository;
    }

    public College collegeA(University university) {
        return collegeRepository.save(College.createCollege(university, "전기전자정보통신공학부"));
    }
}
