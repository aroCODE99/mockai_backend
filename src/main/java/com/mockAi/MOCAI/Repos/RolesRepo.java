package com.mockAi.MOCAI.Repos;

import com.mockAi.MOCAI.Entites.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepo extends JpaRepository<Roles, Long> {

    Optional<Roles> findByRoleName(String roleName);
}
