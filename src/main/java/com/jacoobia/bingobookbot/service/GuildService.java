package com.jacoobia.bingobookbot.service;

import com.jacoobia.bingobookbot.model.entities.*;
import com.jacoobia.bingobookbot.model.repository.GuildRepository;
import com.jacoobia.bingobookbot.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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

    private final GuildRepository guildRepository;

    /**
     * Enter a guild into the active guilds registry
     * @param guild the discord guild to register
     */
    public void registerGuild(Guild guild) {
        String id = guild.getId();

        if (!ACTIVE_GUILDS.containsKey(id)) {
            BingoGuild bingoGuild = guildRepository.getBingoGuildByGuildId(id);
            if(bingoGuild == null) {
                bingoGuild = new BingoGuild();
                bingoGuild.setGuildId(id);
                bingoGuild.setBingoRunning(false);
            }
            if(bingoGuild.getChannelId() != null)
                bingoGuild.setChannel(guild.getTextChannelById(bingoGuild.getChannelId()));
            bingoGuild.setGuild(guild);
            guildRepository.save(bingoGuild);
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
        guildRepository.save(bingoGuild);
        bingoGuild.sendMessage("Gotcha! This will now be the bingo channel!");
    }

    /**
     * Add items to a guild's bingo board item list
     * @param guildId the id of the guild to add an item to
     * @param item the item to add
     * @return was it successful
     */
    public boolean addItem(String guildId, Item item) {
        BingoGuild guild = getGuildById(guildId);
        List<Item> items = new ArrayList<>();
        if(guild.getItems() != null) items.addAll(guild.getItems());

        if(!items.contains(item)) {
            items.add(item);
            guild.setItems(items);
            guildRepository.save(guild);
            return true;
        }
        return false;
    }

    /**
     * Adds a new {@link SkillTarget} to a guild's skill list 
     * @param guild the guild to add to
     * @param target the target 
     * @return was it successful
     */
    public boolean addSkill(BingoGuild guild, SkillTarget target) {
        List<SkillTarget> targets = new ArrayList<>();
        if(guild.getItems() != null) targets.addAll(guild.getSkills());

        if(!targets.contains(target)) {
            targets.add(target);
            guild.setSkills(targets);
            guildRepository.save(guild);
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
            bingoGuild.sendMessage("The bingo event %s is already active!", bingoGuild.getBingoName());
            return;
        }
        bingoGuild.setBingoName(name);
        guildRepository.save(bingoGuild);
        bingoGuild.sendMessage("You have set the Bingo event name to `%s`", name);
    }

    /**
     * 
     * @param guild
     */
    public void startBingo(BingoGuild guild) {
        if(guild.getBingoRunning()) {
            guild.sendMessage("The bingo event %s is already active!", guild.getBingoName());
            return;
        }
        guild.setBingoRunning(true);
        guild.setSecret(RandomStringUtils.randomAlphanumeric(SECRET_LENGTH));
        guildRepository.save(guild);
        guild.sendMessage("Hey @everyone!\\nThe Bingo event %s is now live! Good luck to all!\\nThe secret key is: `%s`\\nGenerating board...", guild.getBingoName(), guild.getSecret());
    }

    /**
     * 
     * @param guildId
     */
    public void stopBingo(String guildId) {
        BingoGuild bingoGuild = getGuildById(guildId);
        bingoGuild.setBingoRunning(false);
        //bingoGuild.setItems(Collections.emptyList());
        bingoGuild.setBingoName(null);
        guildRepository.save(bingoGuild);
    }

    public void deregisterUser(BingoGuild guild, User user) {
        if(!isUserRegistered(guild, user)) {
            guild.sendMessage("You're not registered for the next bingo event, %s!", user.getRsName());
            return;
        }

        List<User> users = getRegisteredUsers(guild);

        users.remove(user);
        guild.setUsers(users);
        guildRepository.save(guild);
        guild.sendMessage("Gotcha! You're no longer registered for the next bingo event, %s!", user.getRsName());
    }

    public void registerUser(BingoGuild guild, User user) {
        if(isUserRegistered(guild, user)) {
            guild.sendMessage("You're already registered for this bingo, %s!", user.getRsName());
            return;
        }

        List<User> users = getRegisteredUsers(guild);

        users.add(user);
        guild.setUsers(users);
        guildRepository.save(guild);
        guild.sendMessage("Gotcha! You've registered to join the bingo, %s!", user.getRsName());
    }

    public void teardown(BingoGuild guild) {
        guild.setSecret(StringUtils.BLANK);
        guild.setBingoName(StringUtils.BLANK);
        guild.setBingoRunning(false);
        guild.setItems(Collections.emptyList());
        guild.setUsers(Collections.emptyList());
        guildRepository.save(guild);
    }

    private boolean isUserRegistered(BingoGuild guild, User user) {
        List<User> users = getRegisteredUsers(guild);
        for(User bingoUser : users) {
            if(bingoUser.getDiscordId().equalsIgnoreCase(user.getDiscordId())
            || bingoUser.getRsName().equalsIgnoreCase(user.getRsName()))
                return true;
        }
        return false;
    }

    /**
     * Gets all the suers currently associated to a guild with a given ID
     * @param guild the guild
     * @return the list of users
     */
    private List<User> getRegisteredUsers(BingoGuild guild) {
        List<User> users = new ArrayList<>();
        if(guild.getUsers() != null)
            users.addAll(guild.getUsers());
        return users;
    }

    public void clearSkillTargets(Guild guild) {
        BingoGuild bingoGuild = getGuildById(guild.getId());
        int count = bingoGuild.getSkills().size();
        bingoGuild.setSkills(Collections.emptyList());
        guildRepository.save(bingoGuild);
        bingoGuild.sendMessage("Cleared out %s skills from the bingo event!", count);
    }

    /**
     * Gets a {@link BingoGuild} object from the discord guild ID
     * @param id the discord guild id
     * @return the BingoGuild object
     */
    public BingoGuild getGuildById(String id) {
        return ACTIVE_GUILDS.get(id);
    }

    public List<BingoGuild> getGuildsUserIsIn(String userId) {
        List<BingoGuild> guilds = new ArrayList<>();
        for (BingoGuild guild : ACTIVE_GUILDS.values()) {
            List<Member> members = guild.getGuild().getMembers();
            for (Member member : members) {
                if (member.getId().equalsIgnoreCase(userId)) {
                    guilds.add(guild);
                }
            }
        }
        return guilds;
    }

}
