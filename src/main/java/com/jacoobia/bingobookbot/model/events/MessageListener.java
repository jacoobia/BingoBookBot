package com.jacoobia.bingobookbot.model.events;

import com.jacoobia.bingobookbot.model.commands.Command;
import com.jacoobia.bingobookbot.model.commands.CommandProcessor;
import com.jacoobia.bingobookbot.model.commands.processors.BingoCommandProcessor;
import com.jacoobia.bingobookbot.model.messages.MessageHelper;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.jacoobia.bingobookbot.utils.CommandUtils.COMMAND_PREFIX;

public class MessageListener extends ListenerAdapter {

    private static List<CommandProcessor> commandProcessors = new ArrayList<>();

    private final MessageHelper messageHelper = new MessageHelper();

    static {
        commandProcessors.add(new BingoCommandProcessor());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().startsWith(COMMAND_PREFIX)) {
            Command command = messageHelper.parseCommand(event);
            for (CommandProcessor processor : commandProcessors) {
                if (processor.relevantCommand(command)) {
                    processor.process(command);
                }
            }
        }
    }

}
