package com.jacoobia.bingobookbot.model.security;

import com.jacoobia.bingobookbot.model.security.user.AuthFailureHandler;
import com.jacoobia.bingobookbot.model.security.user.AuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] ANON_PERMITTED = { "/index", "/", "/webjars/**", "/css/**", "/img/**", "/js/**", "/favicon.ico", "/bingo/**"};

    private final AuthSuccessHandler authSuccessHandler;
    private final AuthFailureHandler authFailureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable();

        http.oauth2Login()
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler);

        http.authorizeRequests()
                .antMatchers(ANON_PERMITTED).permitAll()
                .anyRequest().authenticated();

        http.logout()
                .logoutSuccessUrl("/")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
    }

}
