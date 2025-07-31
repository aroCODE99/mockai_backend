package com.mockAi.MOCAI.Repos;

import com.mockAi.MOCAI.Entites.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepo extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByUserEmail(String userEmail); // finding the user by userEmail

    boolean existsByUserEmail(String email);
}
