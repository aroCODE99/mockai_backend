package com.mockAi.MOCAI.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "token", nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime expiryDate;

    @PrePersist
    public void handleCreatedAt() {
        this.createdAt = LocalDateTime.now();
        this.expiryDate = this.createdAt.plusDays(1);
    }

    @PreUpdate
    public void handleUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

}
