package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByName(String name);
    Long countRoleByName(String name);
}
