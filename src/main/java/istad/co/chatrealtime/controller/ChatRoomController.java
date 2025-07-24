package istad.co.chatrealtime.controller;


import istad.co.chatrealtime.dto.ChatRoomDto;
import istad.co.chatrealtime.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/direct")
    public ResponseEntity<ChatRoomDto> createDirectChatRoom(@RequestBody Map<String, Long> request) {
        Long user1Id = request.get("user1Id");
        Long user2Id = request.get("user2Id");
        ChatRoomDto chatRoom = chatRoomService.createDirectChatRoom(user1Id, user2Id);
        return ResponseEntity.ok(chatRoom);
    }

    @PostMapping("/group")
    public ResponseEntity<ChatRoomDto> createGroupChatRoom(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        Long createdBy = Long.valueOf(request.get("createdBy").toString());
        @SuppressWarnings("unchecked")
        List<Long> memberIds = (List<Long>) request.get("memberIds");

        ChatRoomDto chatRoom = chatRoomService.createGroupChatRoom(name, createdBy, memberIds);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDto> getChatRoomById(@PathVariable Long id) {
        return chatRoomService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatRoomDto>> getUserChatRooms(@PathVariable Long userId) {
        List<ChatRoomDto> chatRooms = chatRoomService.findUserChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/{roomId}/members/{userId}")
    public ResponseEntity<ChatRoomDto> addUserToRoom(
            @PathVariable Long roomId,
            @PathVariable Long userId) {
        ChatRoomDto chatRoom = chatRoomService.addUserToRoom(roomId, userId);
        return ResponseEntity.ok(chatRoom);
    }
}
