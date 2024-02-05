/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alok91340.gethired.entities.Role;

/**
 * @author alok91340
 *
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}

