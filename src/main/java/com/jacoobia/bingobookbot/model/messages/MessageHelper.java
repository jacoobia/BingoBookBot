package com.jacoobia.bingobookbot.model.messages;

import com.jacoobia.bingobookbot.model.commands.Command;
import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import com.jacoobia.bingobookbot.service.GuildService;
import com.jacoobia.bingobookbot.utils.SpringContext;
import com.jacoobia.bingobookbot.utils.StringUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MessageHelper {

    private static final int MAX_ARG_COUNT = 20;

    private final GuildService guildService;

    public MessageHelper() {
        guildService = SpringContext.getBean(GuildService.class);
    }

    /**
     * Extracts data from a message sent in a discord text channel and then
     * creates a {@link Command} object for that message
     *
     * @param event the message event
     * @return a newly constructed command object
     */
    public Command parseCommand(GuildMessageReceivedEvent event) {
        BingoGuild guild = guildService.getGuildById(event.getGuild().getId());
        Message message = event.getMessage();
        Command command = new Command();
        String commandName = splitMessage(message)[0].replace("!", StringUtils.BLANK);
        command.setName(commandName);
        command.setGuild(event.getGuild());
        command.setArgs(getArgs(message));
        command.setChannel(event.getChannel());
        command.setMember(event.getMember());
        command.setBingoGuild(guild);
        return command;
    }

    /**
     * Gets the arguments from a !command message sent
     *
     * @param message the message to extract the arguments from
     * @return a string array of the arguments
     */
    private String[] getArgs(Message message) {
        String[] split = splitMessage(message);
        String[] args = new String[Math.min(split.length - 1, MAX_ARG_COUNT)];
        for(int i = 1; i < split.length; i++) {
            int argsIndex = i - 1;
            if(argsIndex < MAX_ARG_COUNT)
                args[argsIndex] = split[i];
        }
        return args;
    }


    /**
     * Splits a message into individual strings
     *
     * @param message the message to split
     * @return a string array of the parts
     */
    public String[] splitMessage(Message message) {
        return message.getContentRaw().split(" ");
    }

}