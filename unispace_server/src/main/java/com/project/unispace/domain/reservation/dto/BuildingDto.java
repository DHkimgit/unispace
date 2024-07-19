package com.project.unispace.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
public class BuildingDto {

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CreateBuilding {
        private String name;
        private String location;
        private Integer aboveGroundFloors;
        private Integer underGroundFloors;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime openTime;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime closeTime;

        private Set<String> openDays;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetBuilding {
        private String name;
        private String location;
        private Integer aboveGroundFloors;
        private Integer underGroundFloors;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime openTime;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime closeTime;

        private Set<String> openDays;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response {
        private String status;
        private String message;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class BuildingResponse {
        private Long id;
        private String name;
    }
}
