package com.codeicontech.hms.data.repositories;

import java.util.Optional;

import com.codeicontech.hms.data.constants.AppRole;
import com.codeicontech.hms.data.models.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole roleName);
}
