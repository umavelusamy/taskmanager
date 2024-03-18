package com.taskmanager.taskmanagerapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import com.taskmanager.taskmanagerapp.utils.AppUtils;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RolesAllowed({ "ADMIN" })
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<User> users = userRepository.findAll();
            return AppUtils.httpResponseOk(users);
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        try {
            // encode user password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userPostCreation = userRepository.save(user);
            return AppUtils.httpResponseOk(userPostCreation);
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }
    }

    @PutMapping
    public ResponseEntity<?> update(Authentication authToken, @Valid @RequestBody User user) {
        if (user.getId() == null) {
            return AppUtils.httpResponseBadRequest("'id' is missing");
        }

        Optional<User> userOrg = userRepository.findById(user.getId());
        if (!userOrg.isPresent()) {
            return AppUtils.httpResponseBadRequest("user not found");
        }

        Optional<User> userSelf = userRepository.findByEmail(authToken.getPrincipal().toString());
        if (!userSelf.isPresent()) {
            return AppUtils.httpResponseBadRequest("invalid session");
        }

        if (userSelf.get().getId() == user.getId()) {
            return AppUtils.httpResponseBadRequest("managing self is not allowed here, use profile api");
        }

        // update empty fields
        if (user.getEmail() == null) {
            user.setEmail(userOrg.get().getEmail());
        }
        if (user.getFirstname() == null) {
            user.setFirstname(userOrg.get().getFirstname());
        }
        if (user.getLastname() == null) {
            user.setLastname(userOrg.get().getLastname());
        }
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(userOrg.get().getPassword());
        }

        try {
            // encode user password
            User userPostUpdation = userRepository.save(user);
            return AppUtils.httpResponseOk(userPostUpdation);
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);

        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(value = "id") Integer id) {
        try {
            // encode user password
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                return AppUtils.httpResponseOk(user);
            }
            return AppUtils.httpResponseBadRequest("user not found");

        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }
    }

}
