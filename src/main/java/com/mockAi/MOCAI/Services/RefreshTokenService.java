package com.mockAi.MOCAI.Services;

import com.mockAi.MOCAI.Entites.AppUser;
import com.mockAi.MOCAI.Entites.RefreshToken;
import com.mockAi.MOCAI.Repos.AuthRepo;
import com.mockAi.MOCAI.Repos.RefreshTokenRepo;
import com.mockAi.MOCAI.Security.CustomUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepo tokenRepo;

    private final AuthRepo authRepo;

    public RefreshTokenService(RefreshTokenRepo tokenRepo, AuthRepo authRepo) {
        this.tokenRepo = tokenRepo;
        this.authRepo = authRepo;
    }

    public String generateRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public RefreshToken generateAndSaveRefreshToken(CustomUserDetails userDetails) {
        AppUser user = authRepo.findByUserEmail(userDetails.getUsername()).orElseThrow(
            () -> new UsernameNotFoundException("I know this is not going to throw error because i am using the userDetails")
        );

        Optional<RefreshToken> existedToken = tokenRepo.findByUser(user);

        String token = generateRefreshToken();

        // if the token exists replace with the newToken
        existedToken.ifPresent(tokenRepo::delete);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);

        tokenRepo.save(refreshToken);

        return refreshToken;
    }

    public Optional<RefreshToken> getRefreshTokenForUser(String token) {
        return tokenRepo.findByToken(token);
    }

}


// But what is SecureRandom?
// SecureRandom is Java’s class for generating cryptographically secure random numbers.
// It’s better than java.util.Random for security purposes because:
// It uses unpredictable entropy (possibly from OS sources like /dev/urandom or hardware RNGs).
// Suitable for things like tokens, passwords, keys, salts, etc.
