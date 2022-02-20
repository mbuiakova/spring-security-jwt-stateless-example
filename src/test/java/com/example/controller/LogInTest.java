package com.example.controller;

import com.example.config.SecurityConfig;
import com.example.domain.UserDetailAuth;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.JwtUtil;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.Optional;

import static com.example.controller.TestDataFactory.getUser;
import static com.example.controller.TestDataFactory.getUserDetails;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(LogInTest.class)
@AutoConfigureMockMvc()
class LogInTest {

    private static final String LOGIN = "/login";
    User user = getUser();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserService userService;

    @MockBean
    JwtUtil jwtUtil;

    @Test
    void shouldReturn401IfJwtNotExist() throws Exception {
        String token = new String(Base64.getEncoder().encode(("aa@email.com:" + "123").getBytes()));
        mockMvc.perform(get("/hello").header("Authorization", "Basic " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnIsOkIfRequestIsCorrect() throws Exception {
        UserDetailAuth userDetail = getUserDetails();
        user.setEnabled(true);
        when(userRepository.findByEmailWithRoles(anyString())).thenReturn(Optional.of(user));
        when(userService.loadUserByUsername(user.getEmail())).thenReturn(userDetail);

        String token = new String(Base64.getEncoder().encode(("bb@email.com:" + "123").getBytes()));

        mockMvc.perform(post(LOGIN).header("Authorization", "Basic " + token))
                .andExpect(status().isOk());
    }

}