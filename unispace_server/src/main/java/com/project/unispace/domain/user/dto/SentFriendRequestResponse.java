package com.project.unispace.domain.user.dto;

import com.project.unispace.domain.user.entity.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SentFriendRequestResponse {
    private Long receiveUserId;
    private String receiveUserNickname;
    private FriendStatus status;
}
