package com.project.unispace.domain.reservation.controller;

import com.project.unispace.domain.reservation.dto.BuildingDto;
import com.project.unispace.domain.reservation.dto.PolicyDto;
import com.project.unispace.domain.reservation.dto.RoomDto;
import com.project.unispace.domain.reservation.entity.Building;
import com.project.unispace.domain.reservation.service.BuildingService;
import com.project.unispace.domain.reservation.service.PolicyService;
import com.project.unispace.domain.reservation.service.RoomService;
import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.entity.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationAdminController {
    private final BuildingService buildingService;
    private final RoomService roomService;
    private final PolicyService policyService;

    @PostMapping("/admin/building")
    public ResponseEntity<?> createBuilding(@RequestBody BuildingDto.CreateBuilding request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(buildingService.createBuilding(request, user));
    }

    @GetMapping("/admin/building/list")
    public ResponseEntity<?> getBuildings(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(buildingService.getBuildings(user));
    }

    @GetMapping("/admin/building/{id}")
    public ResponseEntity<?> getBuilding(@PathVariable Integer id,  Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(new Result<>(200, "ok", buildingService.getBuilding(id.longValue(), user.getUniversity())));
    }

    @PostMapping("/admin/room")
    public ResponseEntity<?> createRoom(@RequestBody RoomDto.CreateRoom request, Authentication authentication) {
        try{
            Long roomId = roomService.createRoom(request);
            return ResponseEntity.ok(new Result<>(200, "ok", roomId));
        } catch(EntityNotFoundException e){
            return ResponseEntity.ok(new Result<>(500, "error", "Building Not Found"));
        }
        //return ResponseEntity.ok(new Result<>(200, "ok", roomService.createRoom(request)));
    }

    @PostMapping("/admin/room/policy")
    public ResponseEntity<?> createRoomPolicy(@RequestBody PolicyDto.CreatePolicy request, Authentication authentication){
        return ResponseEntity.ok(new Result<>(200, "ok", policyService.createPolicy(request)));
    }

    @GetMapping("/admin/room/list")
    public ResponseEntity<?> getAllRoomByUniv(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(new Result<>(200, "ok", roomService.getAllRoomByUniv(user.getUniversity().getId())));
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int status;
        private String message;
        private T data;

        public Result(int status, String message){
            this.status = status;
            this.message = message;
        }
    }
}
