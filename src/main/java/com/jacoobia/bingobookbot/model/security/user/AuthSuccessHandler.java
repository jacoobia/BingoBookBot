package com.jacoobia.bingobookbot.model.security.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REDIRECTION_URL = "/discord";
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        //todo load/create our own user object etc...
        redirect(request, response, authentication);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            if (response.isCommitted()) {
                logger.debug("Response has already been committed. Unable to redirect");
                return;
            }
            redirectStrategy.sendRedirect(request, response, REDIRECTION_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
