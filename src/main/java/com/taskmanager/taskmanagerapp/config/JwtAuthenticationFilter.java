package com.taskmanager.taskmanagerapp.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.repository.UserRepository;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepo;

    private boolean isPublicEndpoint(HttpServletRequest request) {
        for (String publicEndpoint : AppConstants.PUBLIC_ENDPOINTS) {
            if (new AntPathRequestMatcher(publicEndpoint).matches(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain)
            throws ServletException, IOException {

        // if request is a public endpoint, skip auth verification
        if (isPublicEndpoint(request)) {
            // proceed with other filters
            filterChain.doFilter(request, response);
            return;
        }

        // verify the token presence
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            markBadToken(response, "jwt token is missing");
            return;
        }

        // get the jwt token
        final String jwtToken = authHeader.substring(7);

        // verify the token validity
        if (jwtService.isTokenExpired(jwtToken)) {
            markBadToken(response, "invalid jwt token");
            return;
        }

        // verify the token has valid email address
        final String userEmail = jwtService.extractSubject(jwtToken);
        if (userEmail == null) {
            markBadToken(response, "invalid jwt token");
            return;
        }

        // verify user details from the database on each request
        Optional<User> actualUser = userRepo.findByEmail(userEmail);
        if (!actualUser.isPresent()) {
            markBadToken(response, "invalid jwt token");
            return;
        }

        // include authenication into security context
        // update user roles from the database user
        JwtAuthenticationToken authToken = new JwtAuthenticationToken(actualUser.get().getEmail(),
                AuthorityUtils.createAuthorityList(actualUser.get().getRole().name()));
        SecurityContext newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(newContext);

        // proceed with other filters
        filterChain.doFilter(request, response);
    }

    private void markBadToken(HttpServletResponse response, String errMsg) throws IOException{
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.addHeader("Content-Type", "text/plain");
            response.getWriter().println(errMsg);
    }

}
