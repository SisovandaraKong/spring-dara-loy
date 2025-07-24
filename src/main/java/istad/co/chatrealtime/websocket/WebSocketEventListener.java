package istad.co.chatrealtime.websocket;


import istad.co.chatrealtime.domain.User;
import istad.co.chatrealtime.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("Received a new web socket connection with session ID: {}", sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Object userId = headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            Long userIdLong = Long.valueOf(userId.toString());

            // Update user status to offline
            userService.updateUserStatus(userIdLong, User.UserStatus.OFFLINE);

            // Broadcast user leave
            messagingTemplate.convertAndSend("/topic/public",
                    "User " + userId + " left the chat");

            log.info("User {} disconnected from session {}", userId, sessionId);
        }
    }
}