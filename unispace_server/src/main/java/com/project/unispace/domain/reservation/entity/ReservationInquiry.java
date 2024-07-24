package com.project.unispace.domain.reservation.entity;

import com.project.unispace.domain.BaseEntity;
import com.project.unispace.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "RESERVATION_INQUIRIES")
public class ReservationInquiry extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "INQUIRY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESERVATION_ID", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user; // 문의 작성자

    @Column(length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status; // 답변 대기, 답변 완료

    @Column(length = 1000)
    private String adminReply; // 관리자 답변

    private ReservationInquiry(Reservation reservation, User user, String content, InquiryStatus status) {
            this.reservation = reservation;
        this.user = user;
        this.content = content;
        this.status = status;
    }

    public static ReservationInquiry CreateInquiry(Reservation reservation, User user, String content) {
        return new ReservationInquiry(reservation, user, content, InquiryStatus.PENDING);
    }

    public void addReply(String adminReply) {
        this.adminReply = adminReply;
    }
}
