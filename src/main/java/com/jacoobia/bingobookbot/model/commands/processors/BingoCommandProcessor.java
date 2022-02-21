package com.jacoobia.bingobookbot.model.commands.processors;

import com.jacoobia.bingobookbot.annotations.CommandName;
import com.jacoobia.bingobookbot.annotations.SubCommand;
import com.jacoobia.bingobookbot.model.commands.Command;
import com.jacoobia.bingobookbot.model.commands.CommandProcessor;
import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import com.jacoobia.bingobookbot.utils.SpringContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Processes the !bingo commands and pushes it along the line
 * to the relevant subcommand using reflection.
 */
@CommandName("bingo")
public class BingoCommandProcessor implements CommandProcessor {

    private final Map<String, Method> commands = new HashMap<>();
    private final BingoSubCommandProcessor bingoSubCommandProcessor;

    public BingoCommandProcessor() {
        bingoSubCommandProcessor = SpringContext.getBean(BingoSubCommandProcessor.class);

        Class<? extends BingoSubCommandProcessor> clazz = bingoSubCommandProcessor.getClass();
        Method[] methods = clazz.getMethods();

        for(Method method : methods) {
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand annotation = method.getAnnotation(SubCommand.class);
                String[] qualifiers = annotation.value();
                Arrays.stream(qualifiers).forEach(qualifier -> commands.put(qualifier, method));
            }
        }
    }

    @Override
    public void process(Command command) {
        BingoGuild bingoGuild = command.getBingoGuild();

        if(!checkChannel(bingoGuild, command.getArgs()[0])) {
            command.getChannel().sendMessage("Something when fetching your guild details. Please register the Bingo text channel with `!bingo channel` to fix this.").queue();
            return;
        }

        try {
            for (String qualifier : commands.keySet()) {
                if (qualifier.equalsIgnoreCase(command.getArgs()[0])) {
                    commands.get(qualifier).invoke(bingoSubCommandProcessor, command);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkChannel(BingoGuild bingoGuild, String subCommand) {
        if(bingoGuild == null)
            return false;
        if(!subCommand.equalsIgnoreCase("channel")) {
            return bingoGuild.getChannel() != null && bingoGuild.getChannelId() != null;
        }
        else return true;
    }

}
