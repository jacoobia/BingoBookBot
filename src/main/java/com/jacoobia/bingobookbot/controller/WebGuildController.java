package com.jacoobia.bingobookbot.controller;

import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import com.jacoobia.bingobookbot.model.user.UserDetailsHelper;
import com.jacoobia.bingobookbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebGuildController {

    private final UserDetailsHelper userDetailsHelper;
    private final GuildService guildService;

    @PostMapping("test")
    public void test(@RequestBody String id) {
        BingoGuild guild = guildService.getGuildById(id);
        guild.sendMessage("%s clicked card on web portal!", userDetailsHelper.getSessionUserName());
    }

}
