package org.example.docmeet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity
public class SecurityConfig {

//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.builder().username("user").password("password").roles("USER").build());
//        return manager;
//    }
//
//    @Bean
//    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests((authorizeRequests) -> authorizeRequests.anyRequest().permitAll());
//        return http.build();
//    }
}
