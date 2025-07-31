package com.mockAi.MOCAI.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;

    @Column(name = "email", nullable = false)
    private String userEmail;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(name = "oauth_provider", nullable = true)
    private String oauthProvider;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> userRoles = new HashSet<>();

    public AppUser(String userEmail, String password, String username) {
        this.userEmail = userEmail;
        this.password = password;
        this.username = username;
    }

    @PrePersist
    public void handleCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void handleUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

}
