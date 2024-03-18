package com.taskmanager.taskmanagerapp.service;

import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskmanager.taskmanagerapp.config.JwtService;
import com.taskmanager.taskmanagerapp.entity.Role;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.model.AuthenticationRequest;
import com.taskmanager.taskmanagerapp.model.RegisterRequest;
import com.taskmanager.taskmanagerapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    public String authenticate(AuthenticationRequest request) throws Exception {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (!user.isPresent() ||
                !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            throw new BadRequestException("invalid email or password");
        }
        return jwtService.generateToken(user.get());
    }

    public User manageProfile(String email, User manageUser) throws Exception {
        Optional<User> userOrg = userRepository.findByEmail(email);
        if (!userOrg.isPresent()) {
            throw new BadRequestException("user not found");
        }
        User user = userOrg.get();

        if (manageUser.getFirstname() != null) {
            user.setFirstname(manageUser.getFirstname());
        }

        if (manageUser.getLastname() != null) {
            user.setLastname(manageUser.getLastname());
        }

        if (manageUser.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(manageUser.getPassword()));
        }
        final User updatedUser = userRepository.save(user);
        return updatedUser;
    }

}
