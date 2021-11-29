package com.jacoobia.bingobookbot.model.events;

import com.jacoobia.bingobookbot.service.GuildService;
import com.jacoobia.bingobookbot.utils.SpringContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;

import java.util.List;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        registerGuilds(event.getJDA().getGuilds());
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        registerGuild(event.getGuild());
    }

    private void registerGuilds(List<Guild> guilds) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        for (Guild guild : guilds) {
            registerGuild(guild);
        }
        stopwatch.stop();
        System.out.printf("%s guilds registered in %s ms.%n", guilds.size(), stopwatch.getTotalTimeMillis());
    }

    private void registerGuild(Guild guild) {
        GuildService guildService = SpringContext.getBean(GuildService.class);
        guildService.registerGuild(guild);
    }

}
