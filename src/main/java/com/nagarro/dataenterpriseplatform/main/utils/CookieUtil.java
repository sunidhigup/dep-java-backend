package com.nagarro.dataenterpriseplatform.main.utils;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public HttpCookie createAccessTokenCookie(String token, Long duration) {
        return ResponseCookie.from("access_token", token)
                .maxAge(duration)
                .httpOnly(true)
                .path("/")
                .build();
    }
}
