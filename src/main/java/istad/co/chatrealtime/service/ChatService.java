package istad.co.chatrealtime.service;


import istad.co.chatrealtime.domain.ChatMessage;
import istad.co.chatrealtime.domain.ChatRoom;
import istad.co.chatrealtime.domain.User;
import istad.co.chatrealtime.dto.ChatMessageDto;
import istad.co.chatrealtime.repositoy.ChatMessageRepository;
import istad.co.chatrealtime.repositoy.ChatRoomRepository;
import istad.co.chatrealtime.repositoy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatMessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageDto sendMessage(ChatMessageDto messageDto) {
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + messageDto.getRoomId()));

        // Verify sender is member of the room
        if (!chatRoom.getMembers().contains(sender)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to do that");
        }

        ChatMessage message = ChatMessage.builder()
                .content(messageDto.getContent())
                .type(messageDto.getType() != null ? messageDto.getType() : ChatMessage.MessageType.TEXT)
                .sender(sender)
                .chatRoom(chatRoom)
                .build();

        ChatMessage savedMessage = messageRepository.save(message);
        ChatMessageDto savedMessageDto = convertToDto(savedMessage);

        // Send message to all room members via WebSocket
        broadcastMessage(savedMessageDto);

        log.info("Message sent by {} in room {}: {}", sender.getUsername(), chatRoom.getName(), message.getContent());
        return savedMessageDto;
    }

    public List<ChatMessageDto> getChatHistory(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messagesPage = messageRepository.findByRoomIdOrderByTimestampDesc(roomId, pageable);

        // Reverse the order to get chronological order (oldest first)
        List<ChatMessage> messages = messagesPage.getContent();
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ChatMessageDto> getRecentMessages(Long roomId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<ChatMessage> messagesPage = messageRepository.findByRoomIdOrderByTimestampDesc(roomId, pageable);

        return messagesPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatMessageDto editMessage(Long messageId, String newContent) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

        message.setContent(newContent);
        message.setEdited(true);
        message.setEditedAt(LocalDateTime.now());

        ChatMessage updatedMessage = messageRepository.save(message);
        ChatMessageDto updatedMessageDto = convertToDto(updatedMessage);

        // Broadcast the updated message
        broadcastMessageUpdate(updatedMessageDto);

        log.info("Message {} edited by {}", messageId, message.getSender().getUsername());
        return updatedMessageDto;
    }

    private void broadcastMessage(ChatMessageDto message) {
        // Send to specific room topic
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
        log.debug("Broadcasted message to room: {}", message.getRoomId());
    }

    private void broadcastMessageUpdate(ChatMessageDto message) {
        // Send message update to specific room topic
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId() + "/update", message);
        log.debug("Broadcasted message update to room: {}", message.getRoomId());
    }

    private ChatMessageDto convertToDto(ChatMessage message) {
        return ChatMessageDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .type(message.getType())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .senderDisplayName(message.getSender().getDisplayName())
                .roomId(message.getChatRoom().getId())
                .timestamp(message.getTimestamp())
                .edited(message.getEdited())
                .editedAt(message.getEditedAt())
                .build();
    }
}