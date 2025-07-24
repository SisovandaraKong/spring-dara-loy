package istad.co.chatrealtime.repositoy;


import istad.co.chatrealtime.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId ORDER BY cm.timestamp DESC")
    Page<ChatMessage> findByRoomIdOrderByTimestampDesc(@Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId ORDER BY cm.timestamp DESC")
    List<ChatMessage> findByRoomIdOrderByTimestampDesc(@Param("roomId") Long roomId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId ORDER BY cm.timestamp ASC")
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(@Param("roomId") Long roomId);
}
