package istad.co.chatrealtime.service;


import istad.co.chatrealtime.domain.ChatRoom;
import istad.co.chatrealtime.domain.User;
import istad.co.chatrealtime.dto.ChatRoomDto;
import istad.co.chatrealtime.dto.UserDto;
import istad.co.chatrealtime.repositoy.ChatRoomRepository;
import istad.co.chatrealtime.repositoy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoomDto createDirectChatRoom(Long user1Id, Long user2Id) {
        // Check if direct chat already exists
        Optional<ChatRoom> existingRoom = chatRoomRepository.findDirectChatRoom(user1Id, user2Id);
        if (existingRoom.isPresent()) {
            return convertToDto(existingRoom.get());
        }

        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"+ user1Id));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"+ user2Id));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(user1.getDisplayName() + " & " + user2.getDisplayName())
                .type(ChatRoom.RoomType.DIRECT)
                .createdBy(user1Id)
                .members(Set.of(user1, user2))
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        log.info("Created direct chat room between {} and {}", user1.getUsername(), user2.getUsername());
        return convertToDto(savedRoom);
    }

    @Transactional
    public ChatRoomDto createGroupChatRoom(String name, Long createdBy, List<Long> memberIds) {
        List<User> members = userRepository.findAllById(memberIds);
        if (members.size() != memberIds.size()) {
            throw new RuntimeException("Some users not found");
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .type(ChatRoom.RoomType.GROUP)
                .createdBy(createdBy)
                .members(Set.copyOf(members))
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        log.info("Created group chat room: {}", name);
        return convertToDto(savedRoom);
    }

    public Optional<ChatRoomDto> findById(Long id) {
        return chatRoomRepository.findById(id).map(this::convertToDto);
    }

    public List<ChatRoomDto> findUserChatRooms(Long userId) {
        return chatRoomRepository.findByMembersId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomDto addUserToRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        room.getMembers().add(user);
        ChatRoom savedRoom = chatRoomRepository.save(room);
        log.info("Added user {} to room {}", user.getUsername(), room.getName());
        return convertToDto(savedRoom);
    }

    private ChatRoomDto convertToDto(ChatRoom chatRoom) {
        List<UserDto> members = chatRoom.getMembers().stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .displayName(user.getDisplayName())
                        .avatarUrl(user.getAvatarUrl())
                        .status(user.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .type(chatRoom.getType())
                .createdBy(chatRoom.getCreatedBy())
                .createdAt(chatRoom.getCreatedAt())
                .members(members)
                .build();
    }
}