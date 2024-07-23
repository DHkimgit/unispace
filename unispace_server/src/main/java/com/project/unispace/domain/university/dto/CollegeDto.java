package com.project.unispace.domain.university.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
public class CollegeDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class saveRequest{
        @NotBlank(message = "학부의 이름을 입력해주세요")
        private String name;

        @NotBlank(message = "학부가 종속되는 대학의 Id를 입력해주세요")
        private Integer universityId;

    }

    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SaveResponse{
        @JsonProperty("college_id")
        private Long collegeId;
    }


    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CollegeListResponse {
        private List<CollegeResponse> colleges;

        public CollegeListResponse(List<CollegeResponse> colleges) {
            this.colleges = colleges;
        }

        // getter
    }

    @Data
    @Getter @Setter
    @AllArgsConstructor
    public static class CollegeResponse {
        private Long collegeId;
        private String collegeName;
    }
}
