package com.example.config;

import com.example.domain.UserDetailAuth;
import com.example.repository.UserRepository;
import com.example.security.InitialAuthenticationFilter;
import com.example.security.JwtFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> userRepository.findByEmailWithRoles(username).map(UserDetailAuth::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username))));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http = http.cors().and().csrf().disable();

        http = http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        http = http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    log.error("Unauthorized request - {}", authException.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                }).and();

        http.authorizeRequests()
                .antMatchers("/h2-console").permitAll()
                .antMatchers("/hello").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(initialAuthenticationFilter(), JwtFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    JwtFilter jwtTokenFilter() throws Exception {
        JwtFilter jwtFilterBean =
                new JwtFilter(userRepository, new AndRequestMatcher(new AntPathRequestMatcher("/**"),
                        new NegatedRequestMatcher(new AntPathRequestMatcher("/login")),
                        new NegatedRequestMatcher(new AntPathRequestMatcher("/user/registration"))));
        jwtFilterBean.setAuthenticationManager(authenticationManagerBean());
        return jwtFilterBean;
    }

    @Bean
    InitialAuthenticationFilter initialAuthenticationFilter() throws Exception {
        InitialAuthenticationFilter initialAuthenticationFilter = new InitialAuthenticationFilter();
        initialAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return initialAuthenticationFilter;
    }
}
