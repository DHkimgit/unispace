package com.project.unispace.domain.user.service;

import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.university.repository.CollegeRepository;
import com.project.unispace.domain.university.repository.DepartmentRepository;
import com.project.unispace.domain.university.repository.UniversityRepository;
import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.dto.UserDto;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.unispace.domain.user.dto.UserDto.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    @Transactional
    public ResponseTokens saveUserWithCollege(CreateRequest createRequest){
        String encodePassword = passwordEncoder.encode(createRequest.getPassword());
        Optional<University> findUniversity = universityRepository.findById(createRequest.getUniversityId().longValue());
        Optional<College> findCollege = collegeRepository.findById(createRequest.getCollegeId().longValue());
        Optional<Department> findDepartment = departmentRepository.findById(createRequest.getDepartmentId().longValue());

        User user = userRepository.save(User.createUserWithCollege(
                createRequest.getStudentId(), createRequest.getLoginId(),
                encodePassword, createRequest.getUsername(),
                createRequest.getNickname(), createRequest.getPhoneNumber(),
                createRequest.getEmail(), createRequest.getPenaltyScore(),
                createRequest.isEnabled(), findUniversity.get(),
                findCollege.get(), findDepartment.get(), createRequest.getUserRole())
        );

        String jwt = jwtService.generateToken(new UserDetailsImpl(user, user.getLoginId(), user.getPassword()));
        return new ResponseTokens(user.getId(), jwt);
    }

    @Transactional(readOnly = true)
    public boolean checkUserIdDuplicate(String userId){
        return userRepository.existsByLoginId(userId);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserId(),
                        request.getPassword()
                )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(request.getUserId());

        String jwtToken = jwtService.generateToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .userId(userDetails.getUser().getId()).build();
    }

    @Transactional(readOnly = true)
    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with loginId: " + loginId));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public UserDto.SearchResponse findUserByNickname(String nickname){
        User findUser = userRepository.findUserByNickname(nickname).orElseThrow(() -> new EntityNotFoundException("No Exist User with such nickname"));

        return new SearchResponse(findUser.getId(), findUser.getNickname());
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByLoginId(String loginId){
        return userRepository.findUserByLoginId(loginId);
    }

    public List<UserDto.NickNameSearchResponse> searchUsersByNickname(String nickname, Long userId) {
        List<User> users = userRepository.searchUsersByNickname(nickname);
        if(users.isEmpty()) throw new EntityNotFoundException();
        else{
            return users.stream()
                    .filter(user -> !user.getId().equals(userId))
                    .map(user -> {
                        return new UserDto.NickNameSearchResponse(user.getId(), user.getNickname(),
                                user.getUniversity().getId(), user.getUniversity().getName());
                    })
                    .collect(Collectors.toList());
        }
    }

    public UserDataResponse getUserDataById(Long userId) {
        User user = userRepository.findUserById(userId);
        return UserDataResponse.builder()
                .userName(user.getUsername())
                .userId(userId)
                .universityId(user.getUniversity().getId())
                .universityName(user.getUniversity().getName())
                .collegeName(user.getCollege().getName())
                .collegeId(user.getCollege().getId())
                .departmentName(user.getDepartment().getName())
                .departmentId(user.getDepartment().getId())
                .build();
    }
}
