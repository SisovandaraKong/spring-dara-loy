package istad.co.chatrealtime.repositoy;


import istad.co.chatrealtime.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.id = :userId")
    List<ChatRoom> findByMembersId(@Param("userId") Long userId);

    @Query("""
    SELECT cr FROM ChatRoom cr 
    WHERE cr.type = 'DIRECT' 
    AND EXISTS (
        SELECT 1 FROM cr.members m WHERE m.id = :user1Id
    ) 
    AND EXISTS (
        SELECT 1 FROM cr.members m WHERE m.id = :user2Id
    )
    AND SIZE(cr.members) = 2
""")
    Optional<ChatRoom> findDirectChatRoom(@Param("user1Id") Long user1Id,
                                          @Param("user2Id") Long user2Id);

}