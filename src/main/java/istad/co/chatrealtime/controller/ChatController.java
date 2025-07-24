package istad.co.chatrealtime.controller;


import istad.co.chatrealtime.domain.User;
import istad.co.chatrealtime.dto.ChatMessageDto;
import istad.co.chatrealtime.service.ChatService;
import istad.co.chatrealtime.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDto message) {
        try {
            ChatMessageDto sentMessage = chatService.sendMessage(message);
            log.info("Message sent via WebSocket: {}", sentMessage.getContent());
        } catch (Exception e) {
            log.error("Error sending message via WebSocket", e);
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageDto message, SimpMessageHeaderAccessor headerAccessor) {
        // Add user to WebSocket session
        headerAccessor.getSessionAttributes().put("userId", message.getSenderId());

        // Update user status to online
        userService.updateUserStatus(message.getSenderId(), User.UserStatus.ONLINE);

        // Broadcast user join
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
        log.info("User {} joined room {}", message.getSenderUsername(), message.getRoomId());
    }

    @MessageMapping("/chat.typing")
    public void userTyping(@Payload Map<String, Object> typingData) {
        // Broadcast typing indicator to room
        messagingTemplate.convertAndSend("/topic/room/" + typingData.get("roomId") + "/typing", typingData);
        log.debug("User {} is typing in room {}", typingData.get("username"), typingData.get("roomId"));
    }
}