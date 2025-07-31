package com.mockAi.MOCAI.Repos;

import com.mockAi.MOCAI.Entites.AppUser;
import com.mockAi.MOCAI.Entites.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(AppUser user);

}
