package com.project.unispace.domain.university.controller;

import com.project.unispace.domain.university.dto.CollegeDto;
import com.project.unispace.domain.university.dto.CollegeDto.CollegeListResponse;
import com.project.unispace.domain.university.dto.CollegeDto.SaveResponse;
import com.project.unispace.domain.university.repository.CollegeRepository;
import com.project.unispace.domain.university.service.CollegeService;
import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.service.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {
    private final CollegeService collegeService;

    /*
    * [POST] 단과대학 추가
    * */
    @PostMapping("/api/admin/university/manage/college")
    public ResponseEntity<SaveResponse> saveCollege(@RequestBody CollegeDto.saveRequest request){
        return ResponseEntity.ok(collegeService.save(request));
    }

    /*
     * [POST] 단과대학 목록 반환
     * */
    @GetMapping("/api/admin/university/manage/college")
    public ResponseEntity<CollegeListResponse> getCollege(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(collegeService.getCollegesByUserUniversity(userId));
    }
}
