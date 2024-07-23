package com.project.unispace.domain.user.repository;

import com.project.unispace.domain.user.entity.User;

import java.util.List;

public interface UserCustomRepository {
    List<User> searchUsersByNickname(String nickname);
}
