package com.project.unispace.domain.reservation;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.unispace.Fixture.CollegeFixture;
import com.project.unispace.Fixture.DepartmentFixture;
import com.project.unispace.Fixture.UniversityFixture;
import com.project.unispace.Fixture.UserFixture;
import com.project.unispace.domain.reservation.dto.ReservationDto;
import com.project.unispace.domain.university.entity.College;
import com.project.unispace.domain.university.entity.Department;
import com.project.unispace.domain.university.entity.University;
import com.project.unispace.domain.user.entity.User;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserFixture userFixture;

    @Autowired
    private DepartmentFixture departmentFixture;

    @Autowired
    private CollegeFixture collegeFixture;

    @Autowired
    private UniversityFixture universityFixture;

    @Autowired
    private ObjectMapper objectMapper;

    private User userA;
    private Department departmentA;
    private College collegeA;
    private University universityA;
    private String token_userA;

    @BeforeAll
    void setUp() {
        universityA = universityFixture.universityA();
        collegeA = collegeFixture.collegeA(universityA);
        departmentA = departmentFixture.createDepartment(collegeA);
        userA = userFixture.userA(universityA, collegeA, departmentA);
        token_userA = userFixture.getToken(userA);
    }

    @Test
    void 예약을_제출한다() throws Exception {
        ReservationDto.reservationRequest request = new ReservationDto.reservationRequest(
            userA.getId(), Collections.EMPTY_SET, 1L, LocalDate.now().plusDays(4), 1L, "test"
        );
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(
            post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token_userA)
                .content(jsonRequest)
        )
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "status": 200,
              "message": "ok",
              "data": {
                "userId": 6,
                "friends": null,
                "timeSlotId": 1,
                "reserveDate": "2024-09-10",
                "startTime": "08:00:00",
                "endTime": "09:00:00",
                "roomId": 1,
                "buildingName": "인문경영관",
                "roomName": "101호",
                "description": "test"
              }
            }\s
           """));
    }

}
