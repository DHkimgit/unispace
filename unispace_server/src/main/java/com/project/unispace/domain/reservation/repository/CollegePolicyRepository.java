package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.CollegePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegePolicyRepository extends JpaRepository<CollegePolicy, Long> {
}
