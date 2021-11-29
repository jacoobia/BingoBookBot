package com.jacoobia.bingobookbot.model.commands;

import com.jacoobia.bingobookbot.annotations.CommandName;
import com.jacoobia.bingobookbot.utils.StringUtils;

public interface CommandProcessor {

    void process(Command command);

    default boolean relevantCommand(Command command) {
        if(command == null) return false;
        String name = command.getName();
        Class<? extends CommandProcessor> clazz = getClass();
        if(clazz.isAnnotationPresent(CommandName.class) && !StringUtils.isEmpty(name)) {
            CommandName annotation = clazz.getAnnotation(CommandName.class);
            String commandName = annotation.value();
            return commandName.equalsIgnoreCase(name);
        }
        return false;
    }

}
