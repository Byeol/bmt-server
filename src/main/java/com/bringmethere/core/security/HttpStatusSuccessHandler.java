package com.bringmethere.core.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class HttpStatusSuccessHandler implements AuthenticationSuccessHandler {

    private final HttpStatus httpStatus;

    public HttpStatusSuccessHandler(HttpStatus httpStatus) {
        Assert.notNull(httpStatus, "httpStatus cannot be null");
        this.httpStatus = httpStatus;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(httpStatus.value());
    }
}