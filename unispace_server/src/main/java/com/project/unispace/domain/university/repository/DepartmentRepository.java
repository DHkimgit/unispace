package com.project.unispace.domain.university.repository;

import com.project.unispace.domain.university.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("SELECT d FROM Department d " +
            "JOIN FETCH d.college c " +
            "WHERE c.id = :departmentId")
    List<Department> getAllDepartmentByCollegeId(@Param("departmentId") Long departmentId);
}
