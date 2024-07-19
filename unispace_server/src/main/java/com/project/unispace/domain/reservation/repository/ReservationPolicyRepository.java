package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.ReservationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationPolicyRepository extends JpaRepository<ReservationPolicy, Long> {
}
