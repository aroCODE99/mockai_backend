package com.mockAi.MOCAI.Security;

import com.mockAi.MOCAI.Repos.AuthRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthRepo authRepo;

    public CustomUserDetailsService(AuthRepo authRepo) {
        this.authRepo = authRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authRepo.findByUserEmail(username).map(CustomUserDetails::new).orElseThrow(
            () -> new UsernameNotFoundException("User not found")
        );
    }
}
