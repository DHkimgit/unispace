package com.project.unispace.domain.user.entity;

import com.project.unispace.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Friend extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_USER_ID")
    private User requestUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVE_USER_ID")
    private User receiveUser;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    private boolean isSameUniversity;

    @Setter
    private Long rejectUserId;

    @Setter
    private Long acceptUserId;

    private Friend(User requestUser, User receiveUser, FriendStatus status, boolean isSameUniversity) {
        this.requestUser = requestUser;
        this.receiveUser = receiveUser;
        this.status = status;
        this.isSameUniversity = isSameUniversity;
    }

    public static List<Friend> createFriendRequest(User requestUser, User receiveUser, boolean isSameUniversity){
        List<Friend> createdRequests = new ArrayList<>();
        createdRequests.add(new Friend(requestUser, receiveUser, FriendStatus.PENDING, isSameUniversity));
        createdRequests.add(new Friend(receiveUser, requestUser, FriendStatus.REJECTED, isSameUniversity));
        return createdRequests;
    }

    public void acceptRequest(){
        this.status = FriendStatus.ACCEPTED;
    }

    public void rejectRequest(){
        this.status = FriendStatus.REJECTED;
    }

    public void resendRequest() {this.status = FriendStatus.PENDING;}

}
