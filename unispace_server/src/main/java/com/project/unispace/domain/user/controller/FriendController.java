package com.project.unispace.domain.user.controller;

import com.project.unispace.commons.dto.ErrorDto;
import com.project.unispace.commons.exception.AlreadyExistsException;
import com.project.unispace.domain.user.dto.FriendDto;
import com.project.unispace.domain.user.dto.UserDetailsImpl;
import com.project.unispace.domain.user.dto.UserDto;
import com.project.unispace.domain.user.entity.FriendStatus;
import com.project.unispace.domain.user.service.FriendService;
import com.project.unispace.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FriendController {
    private final FriendService friendService;
    private final UserService userService;

    /*
     * 친구 요청 전송
     * */
    @PostMapping("/friend/request/{receiveUserId}")
    public ResponseEntity<?> createFriendRequest(@PathVariable Integer receiveUserId, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        if(receiveUserId.longValue() == userId) return ResponseEntity.status(404).body(new ErrorDto("Receive User Id is same as Request User Id"));
        try{
            return ResponseEntity.ok(friendService.createFriendRequest(receiveUserId.longValue(), userId));
        } catch (AlreadyExistsException e){
            return ResponseEntity.status(409).body(new ErrorDto("Friend request already exists"));
        } catch (Exception e){
            return ResponseEntity.ok(new Result<>(404, e.getMessage()));
        }
    }

    /*
     * 닉네임으로 사용자를 조회
     * */
    @GetMapping("/friend/search/{nickname}")
    public ResponseEntity<?> searchUser(@PathVariable String nickname) {
        try{
            List<UserDto.NickNameSearchResponse> searchResponses = userService.searchUsersByNickname(nickname);
            return ResponseEntity.ok(new Result<>(200, "ok", searchResponses));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.ok(new Result<>(409, "error", "사용자가 존재하지 않습니다"));
        }
    }

    /*
    * 내가 보낸 친구 요청을 조회 (수락 대기중인 요청만 조회)
    * */
    @GetMapping("/friend/requests/sent/before_refactoring")
    public ResponseEntity<?> getSentFriendRequestV2(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(friendService.getSentFriendRequestV2(userId));
    }

    @GetMapping("/friend/requests/sent/after_refactoring")
    public ResponseEntity<?> getSentFriendRequestV3(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(friendService.getSentFriendRequestV3(userId));
    }

    /*
     * 내가 받은 친구 요청을 조회
     * */
    @GetMapping("/friend/requests/receive")
    public ResponseEntity<?> getReceiveFriendRequest(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(friendService.getReceiveFriendRequest(userId));
    }

    /*
     * 내가 받은 친구 요청을 수락
     * */
    @PutMapping("/friend/request/accept/{requestUserId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Integer requestUserId, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long acceptedUserId = userDetails.getUser().getId();
        FriendDto.FriendRequestAcceptResponse response = friendService.acceptFriendRequest(acceptedUserId, requestUserId.longValue());
        if(response.isAlreadyFriend()){
            return ResponseEntity.status(409).body(new ErrorDto("Friend request already accepted"));
        }
        else{
            return ResponseEntity.ok(response);
        }
    }
    /*
     * 내가 받은 친구 요청을 거절
     * */
    @DeleteMapping("/friend/requests/reject/{requestUserId}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable Integer requestUserId, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long acceptedUserId = userDetails.getUser().getId();
        FriendDto.FriendRequestRejectResponse response = friendService.rejectFriendRequest(acceptedUserId, requestUserId.longValue());
        if(response.getRequestStatus() == FriendStatus.PENDING){
            return ResponseEntity.status(409).body(new ErrorDto("Friend request already rejected"));
        }
        else{
            return ResponseEntity.ok(response);
        }
    }

    /*
    * 친구 목록 조회
    * */
    @GetMapping("/friend/list")
    public ResponseEntity<?> getFriendList(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(new Result<>(200, "ok", friendService.getAcceptedFriendList(userId)));
    }

    /*
    * 친구 삭제
    * */
    @DeleteMapping("/friend/delete/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable Integer friendId, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        friendService.deleteFriend(userId, friendId.longValue());
        return ResponseEntity.ok("Delete friend");
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int status;
        private String message;
        private T data;

        public Result(int status, String message){
            this.status = status;
            this.message = message;
        }
    }
}

//@GetMapping("/friend/requests/sent/simple")
//    public ResponseEntity<?> getSentFriendRequestSimple(Authentication authentication){
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        Long userId = userDetails.getUser().getId();
//        return ResponseEntity.ok(friendService.getSentFriendRequestSimple(userId));
//    }
//
//    @GetMapping("/friend/requests/sent/dto")
//    public ResponseEntity<?> getSentFriendRequestWithDto(Authentication authentication){
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        Long userId = userDetails.getUser().getId();
//        return ResponseEntity.ok(friendService.getSentFriendRequestWithDto(userId));
//    }
//
//    @GetMapping("/friend/requests/sent/join-fetch")
//    public ResponseEntity<?> getSentFriendRequestWithFetch(Authentication authentication) {
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        Long userId = userDetails.getUser().getId();
//        return ResponseEntity.ok(friendService.getSentFriendRequestWithFetch(userId));
//    }
