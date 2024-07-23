package com.project.unispace.domain.university.service;

import com.project.unispace.domain.university.dto.UniversityDto;
import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.unispace.domain.university.dto.UniversityDto.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UniversityService {
    private final UniversityRepository universityRepository;
    public List<UniversityResponse> getAllUniversities(){
        return universityRepository.getAllUniversities().stream()
                .map(university -> {
                    return new UniversityResponse(university.getId(), university.getName());
                }).collect(Collectors.toList());
    }
}
