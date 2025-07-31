package com.mockAi.MOCAI.Security;

import com.mockAi.MOCAI.Entites.AppUser;
import com.mockAi.MOCAI.Entites.Roles;
import com.mockAi.MOCAI.Repos.AuthRepo;
import com.mockAi.MOCAI.Repos.RolesRepo;
import com.mockAi.MOCAI.Services.JwtService;
import com.mockAi.MOCAI.Services.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Service
public class OauthSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OauthSuccessHandler.class);
    private final AuthRepo authRepo;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    private final RolesRepo rolesRepo;

    public OauthSuccessHandler(AuthRepo authRepo, RefreshTokenService refreshTokenService, JwtService jwtService,
                               CustomUserDetailsService userDetailsService, RolesRepo rolesRepo) {
        this.authRepo = authRepo;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.rolesRepo = rolesRepo;
    }

    // now what should this do
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String username = Objects.requireNonNull(oAuth2User.getAttribute("given_name")).toString().toLowerCase();
        String oauthProvider = token.getAuthorizedClientRegistrationId();

        CustomUserDetails userDetails;
        try {
            userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        } catch (Exception e) {
            log.error("User Not Found {}", e.getMessage());

            Set<Roles> userRoles = Set.of(
                rolesRepo.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"))
            );

            authRepo.save(new AppUser(email, username, oauthProvider));
            userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        }

        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(userDetails).getToken();
        String jwtAccessToken = jwtService.generateToken(userDetails);

        // ✅ Correct Set-Cookie header
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
            .secure(false) // true in production
            .httpOnly(true)
            .path("/")
            .maxAge(24 * 60 * 60)
            .build();

        response.addHeader("Set-Cookie", cookie.toString());

        // ✅ Send JWT access token to frontend
        String frontendRedirectUrl = "http://localhost:5173/oauth/redirect?token=" + jwtAccessToken;
        response.sendRedirect(frontendRedirectUrl);
    }

}
