package com.project.unispace.domain.user.repository;

import com.project.unispace.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findUserByNickname(String nickname);

    Optional<User> findUserByLoginId(String loginId);

    @Query("SELECT u FROM User u " +
            "JOIN FETCH u.university univ " +
            "JOIN FETCH u.college c " +
            "JOIN FETCH u.department d " +
            "WHERE u.id = :userId")
    User findUserById(@Param("userId") Long userId);

}
