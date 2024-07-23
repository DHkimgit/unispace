package com.project.unispace.domain.university.repository;

import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CollegeRepository extends JpaRepository<College, Long> {
    List<College> findByUniversity(University university);

    @Query("select c from College c where c.university.id = :university_id")
    List<College> findCollegeByUniversityId(@Param("university_id") Long university_id);
}
