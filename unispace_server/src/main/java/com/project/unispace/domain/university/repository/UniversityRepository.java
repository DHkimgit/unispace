package com.project.unispace.domain.university.repository;

import com.project.unispace.domain.university.entity.University;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UniversityRepository extends JpaRepository<University, Long> {
    boolean existsById(Long id);

    @Query("SELECT u FROM University u")
    List<University> getAllUniversities();
}
