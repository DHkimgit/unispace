package com.project.unispace.domain.university.repository;

import com.project.unispace.domain.university.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
