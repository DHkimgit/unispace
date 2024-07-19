package com.project.unispace.domain.reservation.service;

import com.project.unispace.domain.reservation.dto.BuildingDto;
import com.project.unispace.domain.reservation.dto.BuildingDto.CreateBuilding;
import com.project.unispace.domain.reservation.dto.BuildingDto.GetBuilding;
import com.project.unispace.domain.reservation.dto.BuildingDto.Response;
import com.project.unispace.domain.reservation.entity.Building;
import com.project.unispace.domain.reservation.entity.DayOfWeek;
import com.project.unispace.domain.reservation.repository.BuildingRepository;
import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingService {
    private final BuildingRepository buildingRepository;

    public Response createBuilding(CreateBuilding request, User user){
        Building building = Building.createBuilding(request.getName(), request.getLocation(),
                request.getAboveGroundFloors(), request.getUnderGroundFloors(),
                request.getOpenTime(), request.getCloseTime());

        Set<DayOfWeek> openDays = request.getOpenDays().stream()
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());

        building.setOpenDaysPolicy(openDays);
        building.changeUniversity(user.getUniversity());
        buildingRepository.save(building);

        return new Response("200", "Add building Successfully");
    }

    public List<BuildingDto.BuildingResponse> getBuildings(User user) {
        return buildingRepository.getBuildingsByUniversityId(user.getUniversity()).stream()
                .map(building -> {
                    return new BuildingDto.BuildingResponse(building.getId(), building.getName());
                })
                .collect(Collectors.toList());
    }


    public GetBuilding getBuilding(Long buildingId, University univ) {
        Building building = buildingRepository.getBuildingByIdAndUniversity(univ, buildingId);
        Set<String> openDays = building.getOpenDays().stream().map(String::valueOf).collect(Collectors.toSet());
        return new GetBuilding(building.getName(), building.getLocation(), building.getAboveGroundFloors(), building.getUnderGroundFloors(), building.getOpenTime(), building.getCloseTime(), openDays);
    }

    public Building getBuilding(Long id) {
        return buildingRepository.findById(id).orElseThrow();
    }
}
