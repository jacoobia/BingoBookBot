package com.jacoobia.bingobookbot.model.commands;

import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * An entity of our command arguments and a few small
 * operations based on this data.
 */
@Getter
@Setter
public class Command {

    private BingoGuild bingoGuild;
    private Guild guild;
    private MessageChannel channel;
    private Member member;
    private String name;
    private String[] args;

/*    public VoiceChannel getVoiceChannel() {
        return GuildUtils.getMemberVoiceChannel(guild, member);
    }*/

}