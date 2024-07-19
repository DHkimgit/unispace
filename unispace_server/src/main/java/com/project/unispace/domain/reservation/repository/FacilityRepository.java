package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
}
