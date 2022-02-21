package com.jacoobia.bingobookbot.model.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserDetailsHelper {

    private static final String NOT_LOGGED_IN = "anonymousUser";

    private static final String AVATAR_USER = "https://cdn.discordapp.com/avatars/%user_id%/%avatar_id%.png?size=128";

    public String getUserAvatar(Map<String, Object> attributes) {
        String userId = (String) attributes.get("id");
        String avatar = (String) attributes.get("avatar");
        return AVATAR_USER.replace("%user_id%", userId).replace("%avatar_id%", avatar);
    }

    public String getUserId(Map<String, Object> attributes) {
        return (String) attributes.get("id");
    }

    public boolean isUserLoggedIn() {
        return getPrincipal() instanceof DefaultOAuth2User;
    }

    public String getSessionUserName() {
        return (String) getSessionUser().getAttributes().get("username");
    }

    public DefaultOAuth2User getSessionUser() {
        return isUserLoggedIn() ? (DefaultOAuth2User) getPrincipal() : null;
    }

    private Object getPrincipal() {
        return SecurityContextHolder. getContext().getAuthentication().getPrincipal();
    }

}