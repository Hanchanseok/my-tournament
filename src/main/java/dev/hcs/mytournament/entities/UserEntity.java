package dev.hcs.mytournament.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "email")
public class UserEntity {
    private String email;
    private String password;
    private String nickname;
    private LocalDateTime createdAt;
    private boolean isAdmin;
    private boolean isDeleted;
    private boolean isSuspended;
    private boolean isKakao;
}
