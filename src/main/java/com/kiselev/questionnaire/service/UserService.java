package com.kiselev.questionnaire.service;

import com.kiselev.questionnaire.model.User;
import com.kiselev.questionnaire.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(String userId) {
        log.info("Try to get or create User by id: " + userId);
        User user = userRepository.findById(userId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .id(userId)
                            .build();
                    return userRepository.save(newUser);
                });
        log.info("Found or created User: " + user.toString());
        return user;
    }

    public User getUser(String userId) {
        return userRepository.findByIdOrThrow(userId);
    }

}
