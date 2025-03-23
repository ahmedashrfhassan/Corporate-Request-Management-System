package com.warba.assessment.repository;

import com.warba.assessment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCivilId(String civilId);
    boolean existsByCivilId(String civilId);
    Optional<User> findByIdAndDeletedFalse(Long id);
}