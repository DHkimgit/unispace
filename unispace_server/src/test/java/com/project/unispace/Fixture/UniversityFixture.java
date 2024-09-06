package com.project.unispace.Fixture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.university.repository.UniversityRepository;

@Component
public class UniversityFixture {

    private final UniversityRepository universityRepository;

    @Autowired
    public UniversityFixture(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    public University universityA() {
        return universityRepository.save(University.createUniversity("한국기술교육대학교", "천안시 동남구"));
    }
}
