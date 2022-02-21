package com.jacoobia.bingobookbot.model.messages;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Arrays;

/**
 * todo improve and optimise
 */
@Getter
@Setter
public class MessageReceiver implements Serializable {

    @Transient
    private MessageChannel channel;

    /**
     * Sends a generic string message with an attached file payload to a {@link MessageChannel}
     * @param message the message to send to the channel
     * @param title the title for the embedded file message
     * @param payload the payload file data to send
     */
    public void sendMessageWithFile(String message, String title, String payload) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setImage(payload);
        Message build = new MessageBuilder(message).setEmbed(builder.build()).build();
        sendMessage(build);
    }

    /**
     * Send a channel message then follow it up with an image, requires
     * the image to be converted to a byte[] beforehand.
     * @param message the message to send
     * @param fileName the name of the image file
     * @param bytes the byte array data for the image
     */
    public void sendMessageWithImage(String message, String fileName, byte[] bytes) {
        sendMessage(message);
        sendImage(fileName, bytes);
    }

    /**
     * Sends an image file to the discord channel
     * @param fileName the file name
     * @param bytes the image data bytes
     */
    public void sendImage(String fileName, byte[] bytes) {
        channel.sendFile(bytes, fileName + ".png").queue();
    }

    /**
     * Sends a prebuilt embedded message to a {@link MessageChannel}
     * @param message the message to send to the channel
     * @param messageEmbed the prebuilt embedded message to send
     */
    public void sendPreBuiltMessage(String message, MessageEmbed messageEmbed) {
        String text = message == null ? "" : message;
        Message m = new MessageBuilder(text).setEmbed(messageEmbed).build();
        channel.sendMessage(m).queue();
    }

    /**
     * Sends a generic string message to a {@link MessageChannel}
     * @param message the message to send to the channel
     * @param args the string formatter args
     */
    public void sendMessage(String message, Object... args) {
        String finalMessage = String.format(message, args).replace("\\n", "\n");
        sendMessage(finalMessage);
    }

    /**
     * Sends a generic string message to a {@link MessageChannel}
     * @param message the message to send to the channel
     */
    public void sendMessage(String message) {
        channel.sendMessage(message).queue();
    }

    /**
     * Sends a generic string message to a {@link MessageChannel}
     * @param message the message to send to the channel
     */
    public void sendMessage(Message message) {
        channel.sendMessage(message).queue();
    }

    /**
     *
     * Sends multiple generic string messages to a {@link MessageChannel}
     * @param messages the messages to send to the channel
     */
    public void sendMessages(String... messages) {
        Arrays.stream(messages).forEach(this::sendMessages);
    }

}