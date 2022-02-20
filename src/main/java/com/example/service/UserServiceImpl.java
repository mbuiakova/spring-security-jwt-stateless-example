package com.example.service;

import com.example.domain.UserDetailAuth;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        UserDetailAuth userDetailAuth = findByEmail(userEmail)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format("User with userEmail - %s not found", userEmail)));

        return org.springframework.security.core.userdetails.User.withUsername(userEmail)
                .password(userDetailAuth.getPassword())
                .disabled(!userDetailAuth.isEnabled())
                .authorities(userDetailAuth.getAuthorities())
                .build();
    }

    public Optional<UserDetailAuth> findByEmail(String email) {
        return userRepository.findByEmailWithRoles(email).map(UserDetailAuth::new);
    }
}
