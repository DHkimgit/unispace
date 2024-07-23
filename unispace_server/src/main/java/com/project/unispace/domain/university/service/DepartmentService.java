package com.project.unispace.domain.university.service;

import com.project.unispace.domain.university.dto.DepartmentDto;
import com.project.unispace.domain.university.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.unispace.domain.university.dto.DepartmentDto.*;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public List<DepartmentResponse> getAllDepartmentByCollegeId(Long collegeId) {
        return departmentRepository.getAllDepartmentByCollegeId(collegeId).stream()
                .map(department -> {
                    return new DepartmentResponse(department.getId(), department.getName());
                }).collect(Collectors.toList());
    }
}
