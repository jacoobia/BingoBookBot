package com.jacoobia.bingobookbot.model.commands.processors;

import com.jacoobia.bingobookbot.annotations.CommandName;
import com.jacoobia.bingobookbot.annotations.SubCommand;
import com.jacoobia.bingobookbot.model.commands.CommandProcessor;
import com.jacoobia.bingobookbot.model.guild.BingoGuild;
import com.jacoobia.bingobookbot.model.messages.MessageSender;
import com.jacoobia.bingobookbot.service.BingoService;
import com.jacoobia.bingobookbot.utils.SpringContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@CommandName("bingo")
public class BingoCommandProcessor implements CommandProcessor {

    private final Map<String, Method> commands = new HashMap<>();

    private final BingoService bingoService;
    private final MessageSender messageSender;

    public BingoCommandProcessor() {
        bingoService = SpringContext.getBean(BingoService.class);
        messageSender = SpringContext.getBean(MessageSender.class);

        Class<? extends BingoService> clazz = bingoService.getClass();
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
    public void process(com.jacoobia.bingobookbot.model.commands.Command command) {
        String guildId = command.getGuild().getId();
        BingoGuild bingoGuild = bingoService.getGuildById(guildId);

        if(bingoGuild == null || !checkChannel(bingoGuild, command.getArgs()[0])) {
            messageSender.sendMessage(command.getChannel(), "Something when fetching your guild details. Please register the Bingo text channel with `!bingo channel` to fix this.");
            return;
        }

        try {
            for (String qualifier : commands.keySet()) {
                if (qualifier.equalsIgnoreCase(command.getArgs()[0])) {
                    commands.get(qualifier).invoke(bingoService, guildId, command);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkChannel(BingoGuild bingoGuild, String subCommand) {
        if(!subCommand.equalsIgnoreCase("channel")) {
            return bingoGuild.getChannel() != null && bingoGuild.getChannelId() != null;
        }
        else return true;
    }

}
