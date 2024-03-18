package com.taskmanager.taskmanagerapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.model.AuthenticationRequest;
import com.taskmanager.taskmanagerapp.model.RegisterRequest;
import com.taskmanager.taskmanagerapp.service.AuthenticationService;
import com.taskmanager.taskmanagerapp.utils.AppUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            String jwtToken = authenticationService.register(request);
            return AppUtils.httpResponseOk(AppUtils.asHashMap("jwtToken", jwtToken));
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            String jwtToken = authenticationService.authenticate(request);
            return AppUtils.httpResponseOk(AppUtils.asHashMap("jwtToken", jwtToken));
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> manageProfile(Authentication authToken, @Valid @RequestBody User userData) {
        try {
            // to update self profile, take email from authentication token
            userData.setEmail(authToken.getPrincipal().toString());
            User updated = authenticationService.manageProfile(userData.getEmail(), userData);
            return AppUtils.httpResponseOk(updated);
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }
    }
}
