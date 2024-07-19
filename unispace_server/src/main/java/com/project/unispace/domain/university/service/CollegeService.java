package com.project.unispace.domain.university.service;

import com.project.unispace.domain.university.dto.CollegeDto;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.university.repository.CollegeRepository;
import com.project.unispace.domain.university.repository.DepartmentRepository;
import com.project.unispace.domain.university.repository.UniversityRepository;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CollegeService {
    private final DepartmentRepository departmentRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final UserService userService;

    @Transactional
    public CollegeDto.SaveResponse save(CollegeDto.saveRequest request) {
        return universityRepository.findById(request.getUniversityId().longValue())
                .map(university -> {
                    College college = College.createCollege(university, request.getName());
                    College saved = collegeRepository.save(college);
                    return CollegeDto.SaveResponse.builder().collegeId(saved.getId()).build();
                })
                .orElseThrow(() -> new EntityNotFoundException("University not found with id: " + request.getUniversityId()));
    }

    @Transactional(readOnly = true)
    public CollegeDto.CollegeListResponse getCollegesByUserUniversity(String loginId) {
        User user = userService.findByLoginId(loginId);
        University university = user.getUniversity();
        List<College> colleges = collegeRepository.findByUniversity(university);
        return new CollegeDto.CollegeListResponse(colleges.stream()
                .map(college -> new CollegeDto.CollegeResponse(college.getId(), college.getName()))
                .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public CollegeDto.CollegeListResponse getCollegesByUserUniversity(Long userId) {
        User user = userService.findById(userId);
        University university = user.getUniversity();
        List<College> colleges = collegeRepository.findByUniversity(university);
        return new CollegeDto.CollegeListResponse(colleges.stream()
                .map(college -> new CollegeDto.CollegeResponse(college.getId(), college.getName()))
                .collect(Collectors.toList()));
    }

}
