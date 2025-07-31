package com.mockAi.MOCAI.Services;

import com.mockAi.MOCAI.Dtos.Request.LoginRequestDto;
import com.mockAi.MOCAI.Dtos.Request.RegisterRequestDto;
import com.mockAi.MOCAI.Dtos.Response.DataResponse;
import com.mockAi.MOCAI.Dtos.Response.ErrorResponse;
import com.mockAi.MOCAI.Dtos.Response.SuccessResponse;
import com.mockAi.MOCAI.Entites.AppUser;
import com.mockAi.MOCAI.Entites.RefreshToken;
import com.mockAi.MOCAI.Exceptions.EmailAlreadyExistsException;
import com.mockAi.MOCAI.Exceptions.UnauthorizedUserException;
import com.mockAi.MOCAI.Repos.AuthRepo;
import com.mockAi.MOCAI.Repos.RefreshTokenRepo;
import com.mockAi.MOCAI.Security.CustomUserDetails;
import com.mockAi.MOCAI.Security.CustomUserDetailsService;
import com.mockAi.MOCAI.mapper.AuthMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthRepo authRepo;

    private final JwtService jwtService;

    private final AuthenticationManager authManager;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService tokenService;

    private final CustomUserDetailsService userDetailsService;

    public AuthService(AuthRepo authRepo, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, RefreshTokenService tokenService, CustomUserDetailsService userDetailsService) {
        this.authRepo = authRepo;
        this.jwtService = jwtService;
        this.authManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    public ResponseEntity<?> registerUser(RegisterRequestDto user) {
        try {
            // now we need the error for the email already exists
            if (authRepo.existsByUserEmail(user.getEmail())) {
                throw new EmailAlreadyExistsException("Email Already Exists in the database, so we can't register the same user");
            }

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword().strip()));

            authRepo.save(AuthMapper.toEntity(user));

            return ResponseEntity.ok(new SuccessResponse("USER_REGISTERED", LocalDateTime.now().toString()));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(
                    "ERROR", "Error occured while registering the user", LocalDateTime.now().toString()
                )
           );
        }
    }

    public ResponseEntity<?> loginUser(LoginRequestDto user, HttpServletResponse res) {
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            // this means the user is authenticated
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String jwtToken = jwtService.generateToken(userDetails);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            RefreshToken refreshToken = tokenService.generateAndSaveRefreshToken(userDetails);

            Cookie cookie = new Cookie("refresh_token", refreshToken.getToken());
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setHttpOnly(true);

            res.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<String>(jwtToken, "access token", LocalDateTime.now().toString()));

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                "UNAUTHORIZED", "An error occured while authorizing the user", LocalDateTime.now().toString())
            );
        }
    }

    public ResponseEntity<?> refreshAccessToken(String refreshToken) {
        // first check if the refreshToken is there inside the db

        try {
            RefreshToken token = tokenService.getRefreshTokenForUser(refreshToken).orElseThrow(
                () -> new UnauthorizedUserException("User does not have the refreshToken")
            );

            AppUser user = token.getUser();
            // now generateThe AccessToken
            String accessToken = jwtService.generateToken(user);

            return ResponseEntity.status(HttpStatus.OK).body(new DataResponse<String>(
                accessToken, "Generated Access token succesfully", LocalDateTime.now().toString()
            ));

        } catch (UnauthorizedUserException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse("Unauthorized", e.getMessage(), LocalDateTime.now().toString())
            );
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Server Error", "Something went wrong", LocalDateTime.now().toString())
            );
        }
    }

}

// now the thing is that it's not going through the request

// now let's see how the refreshToken service is going to work
// first the userLogins -> generateAccessToken and Then RefreshToken ->
//      save the refreshToken in the db and passed the accessToken