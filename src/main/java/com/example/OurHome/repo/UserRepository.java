package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Role;
import com.example.OurHome.model.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Long countAllByRole(Role role);

    Optional<UserEntity> findByEmail(String Email);

    @Query("SELECT u FROM users u where u.resetCode != null")
    List<UserEntity> findAllUsersWithResetCode();
}
