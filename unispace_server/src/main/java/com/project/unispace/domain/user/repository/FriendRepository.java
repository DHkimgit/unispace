package com.project.unispace.domain.user.repository;

import com.project.unispace.domain.user.dto.FriendDto;
import com.project.unispace.domain.user.dto.SentFriendRequestResponse;
import com.project.unispace.domain.user.entity.Friend;
import com.project.unispace.domain.user.entity.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM Friend f " +
            "WHERE (f.requestUser.id = :userId1 AND f.receiveUser.id = :userId2) " +
            "OR (f.requestUser.id = :userId2 AND f.receiveUser.id = :userId1)")
    List<Friend> findFriendshipBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

//    @Query("SELECT new com.project.unispace.domain.user.dto.FriendDto.FriendPair(f1, f2) "+
//            "FROM Friend f1 " +
//            "JOIN Friend f2 ON f1.receiveUser.id = f2.requestUser.id " +
//            "AND f1.requestUser.id = f2.receiveUser.id " +
//            "WHERE f1.requestUser.id = :userId " +
//            "AND f1.id < f2.id")
//    List<FriendDto.FriendPair> findFriendPairsByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friend f WHERE f.requestUser.id = :userId OR f.receiveUser.id = :userId")
    List<Friend> findAllFriendsByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friend f WHERE f.requestUser.id = :requestUserId AND f.receiveUser.id = :receiveUserID")
    Optional<Friend> findByRequestUserAndReceiveUser(@Param("requestUserId") Long requestUserId, @Param("receiveUserID") Long receiveUserID);

    @Query("SELECT f FROM Friend f WHERE f.requestUser.id = :requestUserId AND f.receiveUser.id = :receiveUserID AND f.status =:status")
    Optional<Friend> findByRequestUserAndReceiveUserAndStatus(@Param("requestUserId") Long requestUserId, @Param("receiveUserID") Long receiveUserID, @Param("status") FriendStatus status);


    @Query("SELECT f FROM Friend f WHERE f.receiveUser.id = :receiveUserId AND f.status = :status")
    List<Friend> findByReceiveUserAndStatus(@Param("receiveUserId") Long receiveUserID, @Param("status")FriendStatus status);

    @Query("SELECT f FROM Friend f WHERE f.requestUser.id = :requestUserId AND f.status = :status")
    List<Friend> findByRequestUserAndStatus(@Param("requestUserId") Long requestUserID, @Param("status")FriendStatus status);

    @Query("SELECT f FROM Friend f JOIN FETCH f.receiveUser WHERE f.requestUser.id = :requestUserId AND f.status IN :statuses")
    List<Friend> findByRequestUserAndStatusWithReceiveUser(@Param("requestUserId") Long requestUserId, @Param("statuses") List<FriendStatus> statuses);

//    @Query("SELECT new com.project.unispace.domain.user.dto.SentFriendRequestResponse(f.receiveUser.id, f.receiveUser.nickname) " +
//            "FROM Friend f " +
//            "JOIN Friend f2 ON f.requestUser.id = f2.receiveUser.id AND f.receiveUser.id = f2.requestUser.id " +
//            "WHERE f.requestUser.id = :userId AND f.status = 'PENDING' AND f2.status = 'REJECTED'")
//    List<SentFriendRequestResponse> findSentFriendRequestsWithDto(@Param("userId") Long userId);

    @Query("SELECT DISTINCT f FROM Friend f " +
            "JOIN FETCH f.receiveUser " +
            "JOIN Friend f2 ON f.requestUser.id = f2.receiveUser.id AND f.receiveUser.id = f2.requestUser.id " +
            "WHERE f.requestUser.id = :userId AND f.status = 'PENDING' AND f2.status = 'REJECTED'")
    List<Friend> findSentFriendRequestsWithFetch(@Param("userId") Long userId);

    @Query("SELECT DISTINCT f FROM Friend f "+
            "JOIN FETCH f.receiveUser " +
            "JOIN Friend f2 ON f.requestUser.id = f2.receiveUser.id AND f.receiveUser.id = f2.requestUser.id "+
            "WHERE (f.requestUser.id = :requestUserId AND f.receiveUser.id = :receiveUserId) OR "
            +"(f.requestUser.id = :receiveUserId AND f.receiveUser.id = :requestUserId)"
    )
    List<Friend> findExistFriendRequest(@Param("requestUserId") Long requestUserId, @Param("receiveUserId") Long receiveUserId);

    @Query("SELECT DISTINCT f FROM Friend f " +
            "JOIN FETCH f.receiveUser " +
            "JOIN Friend f2 ON f.requestUser.id = f2.receiveUser.id AND f.receiveUser.id = f2.requestUser.id " +
            "WHERE ((f.requestUser.id = :requestUserId AND f.receiveUser.id = :receiveUserId) OR " +
            "(f.requestUser.id = :receiveUserId AND f.receiveUser.id = :requestUserId)) AND "+
            "(f.status = 'ACCEPTED' AND f2.status = 'ACCEPTED')"
    )
    List<Friend> findBothAcceptedFriendRecord(@Param("requestUserId") Long requestUserId, @Param("receiveUserId") Long receiveUserId);
}
