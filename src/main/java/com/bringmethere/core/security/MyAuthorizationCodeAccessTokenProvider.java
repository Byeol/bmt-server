package com.bringmethere.core.security;

import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;

public class MyAuthorizationCodeAccessTokenProvider extends AuthorizationCodeAccessTokenProvider {

    public MyAuthorizationCodeAccessTokenProvider() {
        setStateMandatory(false);
    }
}