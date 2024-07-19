package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.DepartmentPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentPolicyRepository extends JpaRepository<DepartmentPolicy, Long> {
}
