package com.bringmethere.config;

import com.bringmethere.user.domain.User;
import com.bringmethere.user.repository.UserRepository;
import com.bringmethere.user.security.SpringSecurityAuditorAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public AuditorAware<User> auditorProvider() {
        return new SpringSecurityAuditorAware(userRepository);
    }
}