package com.project.unispace.domain.reservation.service;

import com.project.unispace.domain.reservation.dto.PolicyDto;
import com.project.unispace.domain.reservation.entity.CollegePolicy;
import com.project.unispace.domain.reservation.entity.DayOfWeek;
import com.project.unispace.domain.reservation.entity.DepartmentPolicy;
import com.project.unispace.domain.reservation.entity.ReservationPolicy;
import com.project.unispace.domain.reservation.repository.CollegePolicyRepository;
import com.project.unispace.domain.reservation.repository.DepartmentPolicyRepository;
import com.project.unispace.domain.reservation.repository.ReservationPolicyRepository;
import com.project.unispace.domain.reservation.repository.RoomRepository;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.university.repository.CollegeRepository;
import com.project.unispace.domain.university.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Transient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final ReservationPolicyRepository reservationPolicyRepository;
    private final CollegePolicyRepository collegePolicyRepository;
    private final DepartmentPolicyRepository departmentPolicyRepository;
    private final RoomRepository roomRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Long createPolicy(PolicyDto.CreatePolicy request){
        return roomRepository.findById(request.getRoomId().longValue())
                .map(room -> {
                    ReservationPolicy policy = ReservationPolicy.createPolicy(room, request.isOpenToPublic(), request.isRequireApproval(), request.getMaxReservationHours(), request.getOpenTime(), request.getReserveCloseTime(), request.getMinDaysBeforeReservation());
                    Set<DayOfWeek> availableDays = request.getAvailableDays().stream()
                            .map(DayOfWeek::valueOf)
                            .collect(Collectors.toSet());
                    policy.addAvailableDayPolicy(availableDays);
                    if(!request.getCollegeRestriction().isEmpty()){
                        Set<CollegePolicy> collegePolicies = request.getCollegeRestriction().stream()
                                .map(collegeId -> {
                                    College foundCollege = collegeRepository.findById(collegeId.longValue()).orElseThrow();
                                    return CollegePolicy.createPolicy(policy, foundCollege);
                                })
                                .collect(Collectors.toSet());
                        policy.setCollegePolicies(collegePolicies);
                    }

                    if(!request.getDepartmentRestriction().isEmpty()) {
                        Set<DepartmentPolicy> departmentPolicies = request.getDepartmentRestriction().stream()
                                .map(departmentId -> {
                                    Department foundDepartment = departmentRepository.findById(departmentId.longValue()).orElseThrow();
                                    return DepartmentPolicy.createPolicy(policy, foundDepartment);
                                })
                                .collect(Collectors.toSet());
                        policy.setDepartmentPolicies(departmentPolicies);
                    }

                    policy.generateTimeSlot();
                    reservationPolicyRepository.save(policy);
                    return policy.getId();
                }).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));
    }



}
