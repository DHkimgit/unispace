package com.project.unispace.domain.university.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DepartmentDto {
    @Data
    @Getter @Setter
    @AllArgsConstructor
    public static class DepartmentResponse {
        private Long departmentId;
        private String departmentName;
    }
}
