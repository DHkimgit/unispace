package com.project.unispace.domain.user.controller;

import com.project.unispace.commons.dto.ErrorDto;
import com.project.unispace.domain.user.dto.FriendDto;
import com.project.unispace.domain.user.dto.UserDto;
import com.project.unispace.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
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

    @PostMapping("/auth/register/college-user")
    public ResponseEntity<UserDto.ResponseTokens> createUser(@Validated @RequestBody UserDto.CreateRequest createRequest){
        return ResponseEntity.ok(userService.saveUserWithCollege(createRequest));
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

}
