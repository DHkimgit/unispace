package com.project.unispace.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Set;

@Data
public class PolicyDto {

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CreatePolicy {
        private Integer roomId;
        // 같은 학생이 아니어도 예약이 가능한가 ?
        private boolean isOpenToPublic;
        // 예약 시 관리자의 승인이 필요한가?
        private boolean requireApproval;
        // 최대 예약할 수 있는 시간 => 예약 타임 자동 생성이 가능한가?
        private Integer maxReservationHours;
        // 시설 예약 시작 시간
        private LocalTime openTime;
        // 시설 예약 종료 시간
        private LocalTime reserveCloseTime;
        // 며칠 전부터 예약이 가능한지
        private Integer minDaysBeforeReservation;
        private Set<String> availableDays;
        private Set<Integer> collegeRestriction;
        private Set<Integer> departmentRestriction;

    }
}
