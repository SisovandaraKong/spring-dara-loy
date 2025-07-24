package istad.co.chatrealtime.dto;


import istad.co.chatrealtime.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private User.UserStatus status;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
}