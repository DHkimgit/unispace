package com.project.unispace.domain.user.repository;

import com.project.unispace.domain.university.entity.QUniversity;
import com.project.unispace.domain.user.entity.Friend;
import com.project.unispace.domain.user.entity.FriendStatus;
import com.project.unispace.domain.user.entity.QFriend;
import com.project.unispace.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendCustomRepositoryImpl implements FriendCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Friend> getAllFriends(Long userId) {
        QUser friendUser = QUser.user; // 친구 요청을 받는 사용자
        QUser requestingUser = new QUser("requestingUser"); // 친구 요청을 하는 사용자에 대한 별칭 생성
        QFriend friend = QFriend.friend;
        QUniversity friendUniversity = QUniversity.university;
        QUniversity requestingUserUniversity = new QUniversity("requestingUserUniversity");

        return jpaQueryFactory
                .selectFrom(friend)
                .leftJoin(friend.receiveUser, friendUser).fetchJoin() // 친구 요청을 받는 사용자 조인
                .leftJoin(friend.requestUser, requestingUser).fetchJoin() // 친구 요청을 하는 사용자 조인
                .leftJoin(friendUser.university, friendUniversity).fetchJoin()
                .leftJoin(requestingUser.university, requestingUserUniversity).fetchJoin() // 요청하는 사용자의 대학 조인
                .where(
                        friend.status.eq(FriendStatus.ACCEPTED)
                                .and(requestingUser.id.eq(userId)) // 요청하는 사용자 ID 조건
                )
                .fetch();
    }
}
