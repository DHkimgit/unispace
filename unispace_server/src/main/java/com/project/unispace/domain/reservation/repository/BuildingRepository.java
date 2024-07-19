package com.project.unispace.domain.reservation.repository;

import com.project.unispace.domain.reservation.entity.Building;
import com.project.unispace.domain.university.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    @Query("SELECT b From Building b WHERE b.university = :univ")
    List<Building> getBuildingsByUniversityId(@Param("univ") University univ);

    @Query("SELECT b FROM Building b WHERE b.university = :univ AND b.id = :id")
    Building getBuildingByIdAndUniversity(@Param("univ") University univ, @Param("id") Long id);
}
