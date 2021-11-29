package com.jacoobia.bingobookbot.model.messages;

import com.jacoobia.bingobookbot.model.guild.BingoGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MessageSender {

    /**
     * Sends a generic string message to a {@link MessageChannel}
     * @param channel the channel to send the message in
     * @param message the message to send to the channel
     */
    public void sendMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    /**
     * Sends a generic string message to a {@link MessageChannel}
     * @param guild the guild to send the message in
     * @param message the message to send to the channel
     * @param args the string formatter args
     */
    public void sendMessage(BingoGuild guild, String message, Object... args) {
        String finalMessage = String.format(message, args).replace("\\n", "\n");
        guild.getChannel().sendMessage(finalMessage).queue();
    }

    /**
     * Sends a generic string message to a {@link MessageChannel}
     * @param guild the guild to send the message in
     * @param message the message to send to the channel
     */
    public void sendMessage(BingoGuild guild, String message) {
        guild.getChannel().sendMessage(message).queue();
    }

    public void sendMessageWithImage(BingoGuild guild, String message, String fileName, byte[] bytes) {
        MessageChannel channel = guild.getChannel();
        channel.sendMessage(message).queue();
        channel.sendFile(bytes, fileName + ".png").queue();
    }

    public void sendImage(BingoGuild guild, String fileName, byte[] bytes) {
        MessageChannel channel = guild.getChannel();
        channel.sendFile(bytes, fileName + ".png").queue();
    }

    /**
     * Sends multiple generic string messages to a {@link MessageChannel}
     * @param guild the guild to send the messages to
     * @param messages the messages to send to the channel
     */
    public void sendMessages(BingoGuild guild, String... messages) {
        Arrays.stream(messages).forEach(s -> sendMessage(guild, s));
    }

    /**
     * Sends a generic string message with an attached file payload to a {@link MessageChannel}
     * @param guild the guild to send the message to
     * @param message the message to send to the channel
     * @param title the title for the embedded file message
     * @param payload the payload file data to send
     */
    public void sendMessageWithFile(BingoGuild guild, String message, String title, String payload) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setImage(payload);
        Message m = new MessageBuilder(message).setEmbed(builder.build()).build();
        guild.getChannel().sendMessage(m).queue();
    }

    /**
     * Sends a prebuilt embedded message to a {@link MessageChannel}
     * @param channel the channel to send the message to
     * @param message the message to send to the channel
     * @param messageEmbed the prebuilt embedded message to send
     */
    public void sendPreBuiltMessage(MessageChannel channel, String message, MessageEmbed messageEmbed) {
        String text = message == null ? "" : message;
        Message m = new MessageBuilder(text).setEmbed(messageEmbed).build();
        channel.sendMessage(m).queue();
    }

    /**
     * Sens a private direct message to a user regardless of the guild the bot
     * is a member of. All it requires is a reference to the {@link User}
     * @param user the user to send the message to
     * @param message the generics string message to send
     */
    public void sendUserMessage(User user, String message) {
        PrivateChannel channel = user.openPrivateChannel().complete();
        sendMessage(channel, message);
    }

    /**
     * Sens a prebuilt embedded private direct message to a user regardless
     * of the guild the bot is a member of. All it requires is a reference to the {@link User}
     * @param user the user to send the message to
     * @param message the generics string message to send
     * @param messageEmbed the prebuilt embedded message to send
     */
    public void sendUserPreBuiltMessage(User user, String message, MessageEmbed messageEmbed) {
        PrivateChannel channel = user.openPrivateChannel().complete();
        sendPreBuiltMessage(channel, message, messageEmbed);
    }


}