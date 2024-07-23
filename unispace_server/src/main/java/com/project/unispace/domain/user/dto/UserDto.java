package com.project.unispace.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.unispace.domain.user.entity.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class UserDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateRequest {

        @Email
        private String email;

        @NotBlank(message = "아이디를 입력해주세요")
        private String loginId;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String password;

        @NotBlank(message = "학번을 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String studentId;

        @NotBlank(message = "이름 입력해주세요.")
        private String username;

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        private String nickname;

        @NotBlank(message = "휴대폰 번호를 입력해주세요.")
        @Pattern(regexp = "(?:(010-\\d{4})|(01[1|6|7|8|9]-\\d{3,4}))-(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
        private String phoneNumber;

        @NotNull(message = "대학 아이디 입력")
        private Integer universityId;

        private Integer collegeId;

        @NotNull(message = "학과 아이디 입력")
        private Integer departmentId;

        private final int penaltyScore = 0;

        private final boolean enabled = true;

        private final UserRole userRole = UserRole.USER;

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginRequest{
        @NotBlank(message = "아이디를 입력해주세요")
        private String loginId;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginResponseDto{
        private String accessToken;
        private String tokenType;
        private Integer userId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseTokens {
        private Long userId;
        private String jwt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthenticationResponse{
        @JsonProperty("access_token")
        private String accessToken;
        private Long userId; // USER_ID 추가
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthenticationRequest {

        private String userId;
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SearchResponse{
        private Long userId;
        private String userNickname;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NickNameSearchResponse{
        private Long userId;
        private String userNickname;
        private Long univId;
        private String universityName;
    }

    @Data
    @Getter @Setter
    @Builder
    public static class UserDataResponse {
        private Long userId;
        private String userName;
        private Long universityId;
        private String universityName;
        private Long collegeId;
        private String collegeName;
        private Long departmentId;
        private String departmentName;
    }

}
