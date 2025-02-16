package com.example.OurHome.security;

import com.example.OurHome.repo.UserRepository;
import com.example.OurHome.service.UserDetailServ;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private final String rememberMeKey;

    public SecurityConfiguration(@Value("${OurHome.remember.me.key}") String rememberMyKey) {
        this.rememberMeKey = rememberMyKey;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(csrf -> csrf.ignoringRequestMatchers("/delete-notification/**")) // 🔹 Игнориране на CSRF само за този ендпойнт
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/index", "/contact").permitAll()
                        .requestMatchers("/register", "/register/**").permitAll()
                        .requestMatchers("/login", "/login/error").permitAll()
                        .requestMatchers("/profile", "/profile/**").authenticated()
                        .requestMatchers("/messages", "/messages/**").authenticated()
                        .requestMatchers("/expenses/details/**").authenticated()
                        .requestMatchers("/property", "/property/**").hasAnyRole("RESIDENT")
                        .requestMatchers("/uploadDocument/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/deleteDocument/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/administration", "/administration/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/delete-notification/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/index")
                        .failureForwardUrl("/login/error")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                )
                .rememberMe(rememberMe -> rememberMe
                        .key(rememberMeKey)
                        .rememberMeParameter("rememberMe")
                        .rememberMeCookieName("rememberMe")
                );

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailServ(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
