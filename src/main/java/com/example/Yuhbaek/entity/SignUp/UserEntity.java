package com.example.Yuhbaek.entity.SignUp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    private String userId;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    private String nickname;

    // ===== 프로필 이미지 추가 =====
    @Column(length = 500)
    private String profileImage;
    // =============================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean surveyCompleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (role == null) {
            role = UserRole.USER;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== 비즈니스 메서드 추가 =====

    /**
     * 프로필 이미지 변경
     */
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * 닉네임 변경
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String password) {
        this.password = password;
    }

    // ================================

    public enum UserRole {
        USER, ADMIN
    }
}