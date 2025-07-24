package istad.co.chatrealtime.service;


import istad.co.chatrealtime.domain.User;
import istad.co.chatrealtime.dto.UserDto;
import istad.co.chatrealtime.repositoy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .displayName(userDto.getDisplayName())
                .avatarUrl(userDto.getAvatarUrl())
                .lastSeen(LocalDateTime.now())
                .status(User.UserStatus.OFFLINE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Created new user: {}", savedUser.getUsername());
        return convertToDto(savedUser);
    }

    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(this::convertToDto);
    }

    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::convertToDto);
    }

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserStatus(Long userId, User.UserStatus status) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(status);
            if (status == User.UserStatus.OFFLINE) {
                user.setLastSeen(LocalDateTime.now());
            }
            userRepository.save(user);
            log.debug("Updated user {} status to {}", user.getUsername(), status);
        });
    }

    public List<UserDto> getOnlineUsers() {
        return userRepository.findByStatus(User.UserStatus.ONLINE).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .lastSeen(user.getLastSeen())
                .createdAt(user.getCreatedAt())
                .build();
    }
}