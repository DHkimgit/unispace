package com.project.unispace.domain.user.service;

import com.project.unispace.commons.exception.AlreadyExistsException;
import com.project.unispace.domain.user.dto.FriendDto;
import com.project.unispace.domain.user.dto.FriendDto.FriendListResponse;
import com.project.unispace.domain.user.dto.FriendDto.FriendRequestResponse;
import com.project.unispace.domain.user.dto.FriendDto.FriendResponse;
import com.project.unispace.domain.user.dto.SentFriendRequestResponse;
import com.project.unispace.domain.user.entity.Friend;
import com.project.unispace.domain.user.entity.FriendStatus;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.repository.FriendRepository;
import com.project.unispace.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {
    public final UserRepository userRepository;
    public final FriendRepository friendRepository;

    public FriendResponse createFriendRequest(Long receiveUserId, Long requestUserId) throws AlreadyExistsException{
        // 1. 서버에 존재하는 회원인지 확인
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + requestUserId));
        User receiveUser = userRepository.findById(receiveUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + receiveUserId));
        // 특수 case 확인
        //Optional<Friend> ExistRequest = friendRepository.findByRequestUserAndReceiveUser(requestUserId, receiveUserId);

        List<Friend> existRequest = friendRepository.findExistFriendRequest(requestUserId, receiveUserId);
        // 이미 관계 데이터가 있는 경우
        if(!existRequest.isEmpty()) {
            if(existRequest.getFirst().getStatus() == FriendStatus.PENDING && existRequest.getFirst().getRequestUser() == receiveUser){
                // 이미 상대방이 나한테 전송한 친구 요청이 있는 경우 -> 상대방의 요청을 자동 수락
                existRequest.getFirst().acceptRequest();
                existRequest.getFirst().setAcceptUserId(requestUserId);
                existRequest.getLast().acceptRequest();
                existRequest.getLast().setAcceptUserId(requestUserId);
                return new FriendResponse(receiveUser.getId());
            } else if (existRequest.getFirst().getStatus() == FriendStatus.ACCEPTED) {
                // 이미 상대방과 친구인 경우 - 요청을 보낼 수 없음
                throw new AlreadyExistsException("Already Friend");
            } else{ // 이미 상대방이 내 요청을 거절한 상태인 경우 - 거절한 시간을 확인하여 5일이 지났을 경우 요청을 다시 전송, 지나지 않았을 경우 요청을 보낼 수 없음
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime updatedDate = existRequest.getFirst().getUpdatedDate();
                if(updatedDate.isBefore(now.minusDays(5))){
                    existRequest.getFirst().resendRequest();
                }
                else{throw new AlreadyExistsException("거절한 사용자에게 다시 요청을 보내려면 5일 경과되어야 함.");}

            }

            throw new AlreadyExistsException("Friend Request Already Exists");
        }
        else {
            // 아무 관계가 없는 경우 - 요청을 전송
            if (Objects.equals(requestUser.getUniversity(), receiveUser.getUniversity())) {
                friendRepository.saveAll(Friend.createFriendRequest(requestUser, receiveUser, true));
            } else {
                friendRepository.saveAll(Friend.createFriendRequest(requestUser, receiveUser, false));
            }
            return new FriendResponse(receiveUser.getId());
        }
    }

    public List<FriendListResponse> getFriendsByUserId(Long userId) {
        List<Friend> allFriends = friendRepository.findAllFriendsByUserId(userId);
        return allFriends.stream()
                .filter(friend -> !friend.getReceiveUser().getId().equals(userId))
                .map(friend -> {
                    String otherUserId = friend.getRequestUser().getId().equals(userId)
                            ? friend.getReceiveUser().getId().toString()
                            : friend.getRequestUser().getId().toString();
                    return new FriendListResponse(otherUserId, friend.getStatus().toString());
                })
                .collect(Collectors.toList());
    }

    /*
     * 내가 전송한 요청 조회 - v1 <PENDING 상태만 조회>
     * */
    public List<SentFriendRequestResponse> getSentFriendRequestV1(Long userId) {
        return friendRepository.findByRequestUserAndStatus(userId, FriendStatus.PENDING).stream()
                .map(friend -> new SentFriendRequestResponse(friend.getReceiveUser().getId(), friend.getReceiveUser().getNickname(), friend.getStatus()))
                .collect(Collectors.toList());
    }

    /*
     * 내가 전송한 요청 조회 - v2 <PENDING 상태와 REJECTED 상태 모두 조회 - N+1 문제 발생>
     * */
    public List<SentFriendRequestResponse> getSentFriendRequestV2(Long userId){
        List<SentFriendRequestResponse> result = new ArrayList<>(friendRepository.findByRequestUserAndStatus(userId, FriendStatus.PENDING).stream()
                .map(friend -> new SentFriendRequestResponse(friend.getReceiveUser().getId(), friend.getReceiveUser().getNickname(), friend.getStatus()))
                .toList());
        friendRepository.findByRequestUserAndStatus(userId, FriendStatus.REJECTED)
                .forEach(friend -> {
                    if (Objects.equals(friend.getRejectUserId(), friend.getReceiveUser().getId())) {
                        result.add(new SentFriendRequestResponse(friend.getReceiveUser().getId(), friend.getReceiveUser().getNickname(), FriendStatus.REJECTED));
                    }
                });
        return result;
    }
    /*
     * 내가 전송한 요청 조회 - v3 <PENDING 상태와 REJECTED 상태 모두 조회 - JOIN FETCH 를 통한 성능 최적화>
     * */
    public List<SentFriendRequestResponse> getSentFriendRequestV3(Long userId) {
        List<Friend> friends = friendRepository.findByRequestUserAndStatusWithReceiveUser(userId, Arrays.asList(FriendStatus.PENDING, FriendStatus.REJECTED));

        return friends.stream()
                .map(friend -> {
                    FriendStatus status = friend.getStatus();
                    if (status == FriendStatus.REJECTED && !Objects.equals(friend.getRejectUserId(), friend.getReceiveUser().getId())) {
                        return null;
                    }
                    return new SentFriendRequestResponse(friend.getReceiveUser().getId(), friend.getReceiveUser().getNickname(), status);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /*
    * 내가 받은 친구 요청 조회
    * */
    public List<FriendDto.ReceiveFriendRequestResponse> getReceiveFriendRequest(Long userId){
        return friendRepository.findByReceiveUserAndStatus(userId, FriendStatus.PENDING).stream()
                .map(friend -> new FriendDto.ReceiveFriendRequestResponse(friend.getRequestUser().getId(), friend.getRequestUser().getNickname()))
                .collect(Collectors.toList());
    }

    public List<FriendDto.AcceptedFriendListResponse> getAcceptedFriendList(Long searchUserId){
        return friendRepository.getAllFriends(searchUserId).stream()
                .map(friendData -> {
                    User friend = friendData.getReceiveUser();
                    User user = friendData.getRequestUser();
                    if(user.getUniversity().equals(friend.getUniversity())) {
                        return new FriendDto.AcceptedFriendListResponse(friend.getId(), friend.getNickname(), friend.getUsername(), friendData.getStatus(), true);
                    }
                    else return new FriendDto.AcceptedFriendListResponse(friend.getId(), friend.getNickname(), friend.getUsername(), friendData.getStatus(), false);
                }).collect(Collectors.toList());
    }

    public FriendDto.FriendRequestAcceptResponse acceptFriendRequest(Long acceptUserId, Long requestUserId){
        Friend requestUserToAcceptUserColumn = friendRepository.findByRequestUserAndReceiveUser(requestUserId, acceptUserId).orElseThrow(() -> new EntityNotFoundException("No Such Friend Request"));
        Friend AcceptUserToRequestUserColumn = friendRepository.findByRequestUserAndReceiveUser(acceptUserId, requestUserId).orElseThrow(() -> new EntityNotFoundException("No Such Friend Request"));
        if(requestUserToAcceptUserColumn.getStatus() == FriendStatus.ACCEPTED || AcceptUserToRequestUserColumn.getStatus()==FriendStatus.ACCEPTED){
            return new FriendDto.FriendRequestAcceptResponse(requestUserToAcceptUserColumn.getRequestUser().getId(), requestUserToAcceptUserColumn.getRequestUser().getNickname(), FriendStatus.ACCEPTED, true);
        } else if (requestUserToAcceptUserColumn.getStatus() == FriendStatus.PENDING && AcceptUserToRequestUserColumn.getStatus() == FriendStatus.REJECTED) {
            requestUserToAcceptUserColumn.acceptRequest();
            AcceptUserToRequestUserColumn.acceptRequest();
            requestUserToAcceptUserColumn.setAcceptUserId(acceptUserId);
            AcceptUserToRequestUserColumn.setAcceptUserId(acceptUserId);
            return new FriendDto.FriendRequestAcceptResponse(requestUserToAcceptUserColumn.getRequestUser().getId(), requestUserToAcceptUserColumn.getRequestUser().getNickname(), FriendStatus.ACCEPTED, false);
        }
        else{
            return new FriendDto.FriendRequestAcceptResponse(requestUserToAcceptUserColumn.getRequestUser().getId(), requestUserToAcceptUserColumn.getRequestUser().getNickname(), FriendStatus.REJECTED, true);
        }
    }

    public FriendDto.FriendRequestRejectResponse rejectFriendRequest(Long rejectUserId, Long requestUserId){
        Friend requestUserToRejectUserColumn = friendRepository.findByRequestUserAndReceiveUser(requestUserId, rejectUserId).orElseThrow(() -> new EntityNotFoundException("No Such Friend Request"));
        Friend rejectUserToRequestUserColumn = friendRepository.findByRequestUserAndReceiveUser(rejectUserId, requestUserId).orElseThrow(() -> new EntityNotFoundException("No Such Friend Request"));
        if (requestUserToRejectUserColumn.getStatus() == FriendStatus.PENDING && rejectUserToRequestUserColumn.getStatus() == FriendStatus.REJECTED) {
            requestUserToRejectUserColumn.rejectRequest();
            requestUserToRejectUserColumn.setRejectUserId(rejectUserId);
            rejectUserToRequestUserColumn.setRejectUserId(rejectUserId);
            return new FriendDto.FriendRequestRejectResponse(requestUserToRejectUserColumn.getRequestUser().getId(), requestUserToRejectUserColumn.getRequestUser().getNickname(), FriendStatus.REJECTED);
        }
        else{
            // 이미 거절 상태인 경우거나 이상한 생태인 경우
            return new FriendDto.FriendRequestRejectResponse(requestUserToRejectUserColumn.getRequestUser().getId(), requestUserToRejectUserColumn.getRequestUser().getNickname(), FriendStatus.PENDING);
        }
    }

    // 친구 관계를 삭제
    public void deleteFriend(Long userId, Long friendId) {
        friendRepository.deleteAll(friendRepository.findBothAcceptedFriendRecord(userId, friendId));
    }
}

