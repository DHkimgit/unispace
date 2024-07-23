package com.project.unispace.domain.university.dto;

import lombok.*;

@Data
public class UniversityDto {
    @Data
    @Getter @Setter
    @AllArgsConstructor
    public static class UniversityResponse {
        private Long universityId;
        private String universityName;
    }
}
