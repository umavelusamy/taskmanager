package com.taskmanager.taskmanagerapp.config;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String useremail;

    public JwtAuthenticationToken(String useremail, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.useremail = useremail;
    }

    @Override
    public Object getCredentials() {
        // we do not have credentials
        return null;
    }

    @Override
    public Object getPrincipal() {
        return useremail;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new IllegalArgumentException("you are not allowed to update authentication status");
    }

}
