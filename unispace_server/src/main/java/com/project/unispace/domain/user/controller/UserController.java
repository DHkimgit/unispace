package com.project.unispace.domain.user.controller;

import com.project.unispace.commons.dto.ErrorDto;
import com.project.unispace.domain.university.service.CollegeService;
import com.project.unispace.domain.university.service.DepartmentService;
import com.project.unispace.domain.university.service.UniversityService;
import com.project.unispace.domain.user.dto.FriendDto;
import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.dto.UserDto;
import com.project.unispace.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final UniversityService universityService;
    private final CollegeService collegeService;
    private final DepartmentService departmentService;

    /*
     * 회원가입
     * */
    @PostMapping("/auth/register/college-user")
    public ResponseEntity<UserDto.ResponseTokens> createUser(@Validated @RequestBody UserDto.CreateRequest createRequest){
        return ResponseEntity.ok(userService.saveUserWithCollege(createRequest));
    }

    @GetMapping("/auth/register/universities")
    public ResponseEntity<?> getUniversities() {
        return ResponseEntity.ok(new Result<>(200, "ok", universityService.getAllUniversities()));
    }

    @GetMapping("/auth/register/colleges/{university_id}")
    public ResponseEntity<?> getColleges(@PathVariable Integer university_id) {
        return ResponseEntity.ok(new Result<>(200, "ok", collegeService.getCollegeByUniversityId(university_id.longValue())));
    }

    @GetMapping("/auth/register/department/{collegeId}")
    public ResponseEntity<?> getDepartments(@PathVariable Integer collegeId) {
        return ResponseEntity.ok(new Result<>(200, "ok", departmentService.getAllDepartmentByCollegeId(collegeId.longValue())));
    }

    /*
    * 로그인
    * */
    @PostMapping("/auth/login")
    public ResponseEntity<UserDto.AuthenticationResponse> authenticate(@RequestBody UserDto.AuthenticationRequest request){
        return ResponseEntity.ok(userService.authenticate(request));
    }

    /*
     * 닉네임으로 User 찾아서 Id 반환
     * */
    @GetMapping("/user/search/{nickname}")
    public ResponseEntity<?> searchFriendByLoginId(@PathVariable String nickname){
        try{
            return ResponseEntity.ok(userService.findUserByNickname(nickname));
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(404).body(new ErrorDto("No Exist User with such nickname"));
        }
    }

    @GetMapping("/user/data")
    public ResponseEntity<?> getUserData(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(new Result<>(200, "ok", userService.getUserDataById(userId)));
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
