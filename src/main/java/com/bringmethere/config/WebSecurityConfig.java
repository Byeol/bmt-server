package com.bringmethere.config;

import com.bringmethere.core.ClientResources;
import com.bringmethere.core.security.HttpStatusSuccessHandler;
import com.bringmethere.core.security.MyAuthorizationCodeAccessTokenProvider;
import com.bringmethere.user.security.SpringDataJpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return new ClientResources();
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(facebook(), "/api/login"));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        AccessTokenProvider accessTokenProvider = new MyAuthorizationCodeAccessTokenProvider();
        restTemplate.setAccessTokenProvider(accessTokenProvider);

        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(restTemplate);

        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        filter.setRestTemplate(restTemplate);
        filter.setTokenServices(tokenServices);
        filter.setApplicationEventPublisher(this.applicationContext);
        filter.setAuthenticationSuccessHandler(new HttpStatusSuccessHandler(HttpStatus.NO_CONTENT));
        return filter;
    }

    @Autowired
    private SpringDataJpaUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // @formatter:off
        auth
                .userDetailsService(this.userDetailsService);
        // @formatter:on
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/").permitAll()
                .and()
                .csrf()
                .disable()
                .headers()
                .frameOptions().disable().and()
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher("/api/**"))
                .and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
        // @formatter:on
    }

}