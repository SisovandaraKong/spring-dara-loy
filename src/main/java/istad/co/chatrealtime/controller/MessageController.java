package istad.co.chatrealtime.controller;


import istad.co.chatrealtime.dto.ChatMessageDto;
import istad.co.chatrealtime.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MessageController {
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody ChatMessageDto messageDto) {
        ChatMessageDto sentMessage = chatService.sendMessage(messageDto);
        return ResponseEntity.ok(sentMessage);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<ChatMessageDto> messages = chatService.getChatHistory(roomId, page, size);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/room/{roomId}/recent")
    public ResponseEntity<List<ChatMessageDto>> getRecentMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "20") int limit) {
        List<ChatMessageDto> messages = chatService.getRecentMessages(roomId, limit);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<ChatMessageDto> editMessage(
            @PathVariable Long messageId,
            @RequestBody Map<String, String> request) {
        String newContent = request.get("content");
        ChatMessageDto editedMessage = chatService.editMessage(messageId, newContent);
        return ResponseEntity.ok(editedMessage);
    }
}