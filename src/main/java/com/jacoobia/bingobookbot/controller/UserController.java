package com.jacoobia.bingobookbot.controller;

import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import com.jacoobia.bingobookbot.model.entities.web.GuildData;
import com.jacoobia.bingobookbot.model.user.UserDetailsHelper;
import com.jacoobia.bingobookbot.service.GuildService;
import com.jacoobia.bingobookbot.service.WebUserService;
import com.jacoobia.bingobookbot.utils.DataExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final WebUserService webUserService;
    private final UserDetailsHelper userDetailsHelper;

    private final GuildService guildService;

    @RequestMapping(value = {"/", "/index"})
    public String index(Model model) {
        if(userDetailsHelper.isUserLoggedIn()) {
            DefaultOAuth2User user = userDetailsHelper.getSessionUser();
            model.addAttribute("username", userDetailsHelper.getSessionUserName());
            model.addAttribute("avatar", userDetailsHelper.getUserAvatar(user.getAttributes()));
            model.addAttribute("guilds", getServers());
        }
        return "index";
    }

    @RequestMapping("/discord")
    public String login() {
        return "discord";
    }

    private List<GuildData> getServers() {
        DefaultOAuth2User user = userDetailsHelper.getSessionUser();
        String id = userDetailsHelper.getUserId(user.getAttributes());
        List<BingoGuild> guilds = guildService.getGuildsUserIsIn(id);
        List<GuildData> data = new ArrayList<>();
        for(BingoGuild guild : guilds) {
            data.add(DataExtractor.extract(guild));
        }
        return data;
    }

}