package com.kiselev.questionnaire.repository;

import com.kiselev.questionnaire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    default User findByIdOrThrow(String id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("User not found by id: " + id));
    }
}
