package com.project.unispace.domain.user.repository;

import com.project.unispace.domain.user.entity.Friend;

import java.util.List;

public interface FriendCustomRepository {
    List<Friend> getAllFriends(Long userId);
}
