package istad.co.chatrealtime.dto;


import istad.co.chatrealtime.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {
    private Long id;
    private String name;
    private ChatRoom.RoomType type;
    private Long createdBy;
    private LocalDateTime createdAt;
    private List<UserDto> members;
    private ChatMessageDto lastMessage;
}