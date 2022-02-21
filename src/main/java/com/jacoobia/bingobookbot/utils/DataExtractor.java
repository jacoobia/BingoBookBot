package com.jacoobia.bingobookbot.utils;

import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import com.jacoobia.bingobookbot.model.entities.web.GuildData;

public class DataExtractor {

    public static GuildData extract(BingoGuild guild) {
        GuildData guildData = new GuildData();
        guildData.setGuildId(guild.getGuildId());
        guildData.setGuildName(guild.getGuild().getName());
        guildData.setBingoActive(guild.getBingoRunning());
        guildData.setGuildMemberCount(guild.getGuild().getMemberCount());
        guildData.setAdminAccess(true);
        guildData.setImageSource(guild.getGuild().getIconUrl());
        return guildData;
    }

}
