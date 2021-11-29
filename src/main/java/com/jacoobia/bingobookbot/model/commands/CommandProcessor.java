package com.jacoobia.bingobookbot.model.commands;

import com.jacoobia.bingobookbot.annotations.Command;
import com.jacoobia.bingobookbot.utils.StringUtils;

public interface CommandProcessor {

    void process(com.jacoobia.bingobookbot.model.commands.Command command);

    default boolean relevantCommand(com.jacoobia.bingobookbot.model.commands.Command command) {
        if(command == null) return false;
        String name = command.getName();
        Class<? extends CommandProcessor> clazz = getClass();
        if(clazz.isAnnotationPresent(Command.class) && !StringUtils.isEmpty(name)) {
            Command annotation = clazz.getAnnotation(Command.class);
            String commandName = annotation.value();
            return commandName.equalsIgnoreCase(name);
        }
        return false;
    }

}
