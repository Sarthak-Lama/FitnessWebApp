package com.project.fitness.service;

import com.project.fitness.dto.RegisterRequest;
import com.project.fitness.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.project.fitness.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User register(RegisterRequest request) {
        User user = new User(
                null,
               request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                Instant.parse("2026-01-08T10:48:41.118Z").atOffset(ZoneOffset.UTC).toLocalDateTime(),
                Instant.parse("2026-01-08T10:48:41.118Z").atOffset(ZoneOffset.UTC).toLocalDateTime(),
                List.of(),
                List.of()

        );

       return userRepository.save(user);

    }
}
