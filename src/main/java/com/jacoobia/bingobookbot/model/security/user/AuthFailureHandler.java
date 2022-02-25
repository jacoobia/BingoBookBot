package com.jacoobia.bingobookbot.model.security.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthFailureHandler.class);

    private static final String AUTH_DENIED = "[access_denied] The resource owner or authorization server denied the request";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if(!AUTH_DENIED.equals(exception.getMessage()))
        {
            final String message = "Exception occurred when trying to authorize a discord login request.";
            logger.error(message, exception);
        }
    }

}
