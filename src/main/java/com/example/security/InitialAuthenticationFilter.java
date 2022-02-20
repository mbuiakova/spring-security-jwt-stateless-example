package com.example.security;

import com.example.domain.UserDetailAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

public class InitialAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtUtil jwtUtil;

    public InitialAuthenticationFilter() {
        super("/login");
        this.setAuthenticationManager(manager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        Authentication authenticate = null;
        try {
            final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            String pair = new String(Base64.getDecoder().decode(authorization.substring(6)));
            String username = pair.split(":")[0];
            String password = pair.split(":")[1];

            authenticate = manager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        } catch (BadCredentialsException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (LockedException e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }

        return authenticate;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) {
        UserDetailAuth user = (UserDetailAuth) authResult.getPrincipal();

        response.setHeader(HttpHeaders.AUTHORIZATION, jwtUtil.generateAccessToken(user));
        response.setStatus(HttpStatus.OK.value());
    }
}
