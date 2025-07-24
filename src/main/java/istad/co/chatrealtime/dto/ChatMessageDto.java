package istad.co.chatrealtime.dto;

import istad.co.chatrealtime.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private String content;
    private ChatMessage.MessageType type;
    private Long senderId;
    private String senderUsername;
    private String senderDisplayName;
    private Long roomId;
    private LocalDateTime timestamp;
    private Boolean edited;
    private LocalDateTime editedAt;
}
