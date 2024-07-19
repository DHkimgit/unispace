package com.project.unispace.domain.user.dto;

import com.project.unispace.domain.user.entity.Friend;
import com.project.unispace.domain.user.entity.FriendStatus;
import lombok.*;

@Data
public class FriendDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateFriendRequest{
        private Long receiverId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FriendResponse{
        private Long receiverId;
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
    public static class FriendPair {
        private Friend friend1;
        private Friend friend2;
    }

    @Getter
    @AllArgsConstructor
    public static class FriendListResponse {
        private String receive_user_id;
        private String status;
    }

    @Getter
    @AllArgsConstructor
    public static class FriendRequestResponse{
        private Long requestUserId;
        private String requestUserNickname;
        private FriendStatus requestStatus;
    }

    @Getter
    @AllArgsConstructor
    public static class AcceptedFriendListResponse{
        private Long friendUserId;
        private String friendUserNickname;
        private FriendStatus friendStatus;
    }

    @Getter
    @AllArgsConstructor
    public static class FriendRequestAcceptResponse{
        private Long requestUserId;
        private String requestUserNickname;
        private FriendStatus requestStatus;
        private boolean isAlreadyFriend;
    }

    @Getter
    @AllArgsConstructor
    public static class FriendRequestRejectResponse{
        private Long requestUserId;
        private String requestUserNickname;
        private FriendStatus requestStatus;
    }

    @Getter
    @AllArgsConstructor
    public static class SentFriendRequestResponse{
        private Long receiveUserId;
        private String receiveUserNickname;
    }

    @Getter
    @AllArgsConstructor
    public static class ReceiveFriendRequestResponse{
        private Long requestUserId;
        private String requestUserNickname;
    }


}
