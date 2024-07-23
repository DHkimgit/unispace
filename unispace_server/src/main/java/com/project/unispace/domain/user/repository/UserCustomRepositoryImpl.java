package com.project.unispace.domain.user.repository;

import com.project.unispace.domain.user.entity.QUser;
import com.project.unispace.domain.user.entity.User;
import com.project.unispace.domain.user.entity.UserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> searchUsersByNickname(String nickname) {
        QUser user = QUser.user;

        return jpaQueryFactory
                .selectFrom(user)
                .where(user.nickname.contains(nickname)
                        .and(user.userRole.eq(UserRole.USER)))
                .fetch();
    }
}
