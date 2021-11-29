package com.jacoobia.bingobookbot;

import com.jacoobia.bingobookbot.model.events.MessageListener;
import com.jacoobia.bingobookbot.model.events.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.EnumSet;

@SpringBootApplication
public class BingoBookBotApplication {

    private static JDA jda;

    public static void main(String[] args) {
        SpringApplication.run(BingoBookBotApplication.class, args);

        try {
            EnumSet<GatewayIntent> intents = EnumSet.of(
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_VOICE_STATES);

            jda = JDABuilder.createLight(args[0], intents)
                    .setActivity(Activity.watching("Castle Wars"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .build();

            registerEvents();
        } catch (Exception e) {
            e.printStackTrace();    //todo log and email me
            System.exit(1);
        }
    }

    private static void registerEvents() {
        jda.addEventListener(new MessageListener());
        jda.addEventListener(new ReadyListener());
    }

}
