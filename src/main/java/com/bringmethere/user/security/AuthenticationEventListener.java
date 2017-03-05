package com.bringmethere.user.security;

import com.bringmethere.user.domain.User;
import com.bringmethere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedPrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@CommonsLog
@Component
public class AuthenticationEventListener {

    private final PrincipalExtractor principalExtractor = new FixedPrincipalExtractor();
    private final UserRepository userRepository;

    private final SpringDataJpaUserDetailsService userDetailsService;

    @EventListener
    public void handleAuthenticationSuccess(InteractiveAuthenticationSuccessEvent event) {
        if (!(event.getAuthentication() instanceof OAuth2Authentication)) {
            return;
        }

        OAuth2Authentication authentication = (OAuth2Authentication) event.getAuthentication();
        authentication.getPrincipal();

        Map<String, Object> map = (Map<String, Object>) authentication.getUserAuthentication().getDetails();

        UserDetails user = getUser(map);

        if (logger.isInfoEnabled()) {
            logger.info("Authenticated user: " + user);
        }

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    private UserDetails getUser(Map<String, Object> map) {
        String username = principalExtractor.extractPrincipal(map).toString();

        UserDetails user;

        try {
            user = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            User newUser = getNewUser(username);

            Optional.ofNullable(map.get("name")).map(Object::toString).ifPresent(newUser::setName);
            Optional.ofNullable(map.get("email")).map(Object::toString).ifPresent(newUser::setEmail);
            Optional.ofNullable(map.get("avatar_url")).map(Object::toString).ifPresent(newUser::setAvatarUrl);

            logger.info("Creating new user: " + username);
            userRepository.save(newUser);

            user = userDetailsService.loadUserByUsername(username);
        }
        return user;
    }

    private User getNewUser(String username) {
        User user = new User(username);
        user.setName(username);

        return user;
    }
}