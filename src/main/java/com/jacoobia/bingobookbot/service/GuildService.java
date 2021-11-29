package com.jacoobia.bingobookbot.service;

import com.jacoobia.bingobookbot.model.entities.BingoItem;
import com.jacoobia.bingobookbot.model.entities.BingoSkillTarget;
import com.jacoobia.bingobookbot.model.entities.BingoUser;
import com.jacoobia.bingobookbot.model.guild.BingoGuild;
import com.jacoobia.bingobookbot.model.messages.MessageSender;
import com.jacoobia.bingobookbot.model.repository.BingoGuildRepository;
import com.jacoobia.bingobookbot.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GuildService {

    public static final Map<String, BingoGuild> ACTIVE_GUILDS = new HashMap<>();

    private static final int SECRET_LENGTH = 6;

    private final BingoGuildRepository bingoGuildRepository;
    private final MessageSender messageSender;

    /**
     * Enter a guild into the active guilds registry
     * @param guild the discord guild to register
     */
    public void registerGuild(Guild guild) {
        String id = guild.getId();

        if (!ACTIVE_GUILDS.containsKey(id)) {
            BingoGuild bingoGuild = bingoGuildRepository.getBingoGuildByGuildId(id);
            if(bingoGuild == null) {
                bingoGuild = new BingoGuild();
                bingoGuild.setGuildId(id);
                bingoGuild.setBingoRunning(false);
            }
            if(bingoGuild.getChannelId() != null)
                bingoGuild.setChannel(guild.getTextChannelById(bingoGuild.getChannelId()));
            bingoGuild.setGuild(guild);
            bingoGuildRepository.save(bingoGuild);
            ACTIVE_GUILDS.put(id, bingoGuild);
        }
    }

    /**
     * Removes a guild from the active guild registry
     * @param guild the guild to deregister
     */
    public void deregisterGuild(BingoGuild guild) {
        ACTIVE_GUILDS.remove(guild.getGuildId());
    }

    /**
     * Gets a {@link BingoGuild} object from the discord guild ID
     * @param id the discord guild id
     * @return the BingoGuild object
     */
    public BingoGuild getGuildById(String id) {
        return ACTIVE_GUILDS.get(id);
    }

    /**
     * Gets a {@link BingoGuild} object from the active events secret
     * @param secret the secret of the event
     * @return the BingoGuild object
     */
    public BingoGuild getBySecret(String secret) {
        for(BingoGuild guild : ACTIVE_GUILDS.values()) {
            if(guild.getSecret() == null)
                continue;
            if(guild.getSecret().equals(secret)) {
                return guild;
            }
        }
        return null;
    }

    /**
     * Set the bingo bot output channel for a specific guild
     * @param guild the guild to set the channel for
     * @param channel the channel selected
     */
    public void setChannel(Guild guild, MessageChannel channel) {
        BingoGuild bingoGuild = getGuildById(guild.getId());

        //If the guild for whatever reason isn't registered then forcefully register it
        if(bingoGuild == null) {
            registerGuild(guild);
            bingoGuild = getGuildById(guild.getId());
        }

        //Set the channel object (temporary) and the channel ID
        bingoGuild.setChannel(channel);
        bingoGuild.setChannelId(channel.getId());

        //Save the guild
        bingoGuildRepository.save(bingoGuild);
    }

    /**
     * Add items to a guild's bingo board item list
     * @param guild the guild to add an item to
     * @param item the item to add
     * @return was it successful
     */
    public boolean addItem(BingoGuild guild, BingoItem item) {
        List<BingoItem> items = new ArrayList<>();
        if(guild.getItems() != null) items.addAll(guild.getItems());

        if(!items.contains(item)) {
            items.add(item);
            guild.setItems(items);
            bingoGuildRepository.save(guild);
            return true;
        }
        return false;
    }

    public boolean addSkill(BingoGuild guild, BingoSkillTarget target) {
        List<BingoSkillTarget> targets = new ArrayList<>();
        if(guild.getItems() != null) targets.addAll(guild.getSkills());

        if(!targets.contains(target)) {
            targets.add(target);
            guild.setSkills(targets);
            bingoGuildRepository.save(guild);
            return true;
        }
        return false;
    }

    /**
     * Set the current bingo event name
     * @param guildId the id of the guild to set the name for
     * @param name the name of the bingo event
     */
    public void setBingoName(String guildId, String name) {
        BingoGuild bingoGuild = getGuildById(guildId);
        if(bingoGuild.getBingoRunning()) {
            messageSender.sendMessage(bingoGuild, "The bingo event %s is already active!", bingoGuild.getBingoName());
            return;
        }
        bingoGuild.setBingoName(name);
        bingoGuildRepository.save(bingoGuild);
        messageSender.sendMessage(bingoGuild, "You have set the Bingo event name to `%s`", name);
    }

    public void startBingo(String guildId) {
        BingoGuild bingoGuild = getGuildById(guildId);
        if(bingoGuild.getBingoRunning()) {
            messageSender.sendMessage(bingoGuild, "The bingo event %s is already active!", bingoGuild.getBingoName());
            return;
        }
        bingoGuild.setBingoRunning(true);
        bingoGuild.setSecret(RandomStringUtils.randomAlphanumeric(SECRET_LENGTH));
        bingoGuildRepository.save(bingoGuild);
        messageSender.sendMessage(bingoGuild, "Hey @everyone!\\nThe Bingo event %s is now live! Good luck to all!\\nThe secret key is: `%s`\\nGenerating board...", bingoGuild.getBingoName(), bingoGuild.getSecret());
    }

    public void stopBingo(String guildId) {
        BingoGuild bingoGuild = getGuildById(guildId);
        bingoGuild.setBingoRunning(false);
        //bingoGuild.setItems(Collections.emptyList());
        bingoGuild.setBingoName(null);
        bingoGuildRepository.save(bingoGuild);
    }

    public void deregisterUser(BingoGuild guild, BingoUser user) {
        if(!isUserRegistered(guild, user)) {
            messageSender.sendMessage(guild, "You're not registered for the next bingo event, %s!", user.getRsName());
            return;
        }

        List<BingoUser> users = getGuildUsers(guild);

        users.remove(user);
        guild.setUsers(users);
        bingoGuildRepository.save(guild);
        messageSender.sendMessage(guild, "Gotcha! You're no longer registered for the next bingo event, %s!", user.getRsName());
    }

    public void registerUser(BingoGuild guild, BingoUser user) {
        if(isUserRegistered(guild, user)) {
            messageSender.sendMessage(guild, "You're already registered for this bingo, %s!", user.getRsName());
            return;
        }

        List<BingoUser> users = getGuildUsers(guild);

        users.add(user);
        guild.setUsers(users);
        bingoGuildRepository.save(guild);
        messageSender.sendMessage(guild, "Gotcha! You've registered to join the bingo, %s!", user.getRsName());
    }

    public void teardown(BingoGuild guild) {
        guild.setSecret(StringUtils.BLANK);
        guild.setBingoName(StringUtils.BLANK);
        guild.setBingoRunning(false);
        guild.setItems(Collections.emptyList());
        guild.setUsers(Collections.emptyList());
        bingoGuildRepository.save(guild);
    }

    private boolean isUserRegistered(BingoGuild guild, BingoUser user) {
        List<BingoUser> users = getGuildUsers(guild);
        for(BingoUser bingoUser : users) {
            if(bingoUser.getDiscordId().equalsIgnoreCase(user.getDiscordId())
            || bingoUser.getRsName().equalsIgnoreCase(user.getRsName()))
                return true;
        }
        return false;
    }

    private List<BingoUser> getGuildUsers(BingoGuild guild) {
        List<BingoUser> users = new ArrayList<>();
        if(guild.getUsers() != null)
            users.addAll(guild.getUsers());
        return users;
    }

}
