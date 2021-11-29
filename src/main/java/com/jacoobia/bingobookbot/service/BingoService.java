package com.jacoobia.bingobookbot.service;

import com.jacoobia.bingobookbot.annotations.SubCommand;
import com.jacoobia.bingobookbot.api.osrs.OsrsClient;
import com.jacoobia.bingobookbot.api.osrsbox.OsrsBoxClient;
import com.jacoobia.bingobookbot.model.commands.Command;
import com.jacoobia.bingobookbot.model.entities.*;
import com.jacoobia.bingobookbot.model.guild.BingoGuild;
import com.jacoobia.bingobookbot.model.messages.MessageSender;
import com.jacoobia.bingobookbot.model.repository.BingoItemRepository;
import com.jacoobia.bingobookbot.model.repository.BingoSkillRepository;
import com.jacoobia.bingobookbot.model.repository.BingoSkillTargetRepository;
import com.jacoobia.bingobookbot.model.repository.BingoUserRepository;
import com.jacoobia.bingobookbot.utils.BoardPainter;
import com.jacoobia.bingobookbot.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BingoService {

    private final GuildService guildService;
    private final MessageSender messageSender;

    private final BingoItemRepository itemRepository;
    private final BingoUserRepository userRepository;
    private final BingoSkillRepository skillRepository;
    private final BingoSkillTargetRepository skillTargetRepository;

    /**
     * Register the bingo bot channel for a guild
     */
    @SubCommand({"channel", "c"})
    public void registerChannel(String guildId, Command command) {
        guildService.setChannel(command.getGuild(), command.getChannel());
        messageSender.sendMessage(command.getChannel(), "Gotcha! This will now be the bingo channel!");
    }

    /**
     * Start the bingo event for a guild
     */
    @SubCommand({"start", "s"})
    public void startBingo(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);
        guildService.startBingo(guild.getGuildId());
        List<BingoItem> items = guild.getItems();
        BoardPainter painter = new BoardPainter();
        items.forEach(painter::add);
        List<BingoSkillTarget> skills = guild.getSkills();
        skills.forEach(painter::add);
        byte[] board = painter.paintAndEncode();
        messageSender.sendImage(guild, guild.getBingoName(), board);
    }

    /**
     * Set the name of a bingo guild
     * @param guildId the discord guild id
     */
    @SubCommand({"name", "n"})
    public void setName(String guildId, Command command) {
        String name = StringUtils.stringArrayToString(command.getArgs(), 1);
        guildService.setBingoName(guildId, name);
    }

    /**
     * Stop the bingo event for a guild
     * @param guildId the id of the guild
     */
    @SubCommand({"stop", "s"})
    public void stopBingo(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);
        if(guild.getBingoRunning()) {
            messageSender.sendMessage(guild, "The bingo event %s has now ended!", guild.getBingoName());
            guildService.stopBingo(guildId);
            //todo print bingo results
        } else {
            messageSender.sendMessage(guild, "There's no bingo event running at the moment!");
        }
    }

    /**
     * Takes in args from the add command, parses them into item names
     * then looks up and adds items to a guild's bingo item list
     * @param guildId the id of the guild
     */
    @SubCommand({"add-item", "ai"})
    public void addItem(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);

        if(guild.getBingoRunning()) {
            messageSender.sendMessage(guild, "You can't add items now! Your bingo event is already running!");
            return;
        }

        String parsed = StringUtils.stringArrayToString(command.getArgs(), 1);
        String[] items = parsed.split(", ");

        for(String item : items) {
            String itemSearch = StringUtils.capitalizeFirst(item);
            BingoItem bingoItem = loadOrCreateBingoItem(itemSearch);

            if(bingoItem == null) {
                messageSender.sendMessage(guild, "Sorry, I couldn't find the item: %s", itemSearch);
                return;
            }

            if(guildService.addItem(guild, bingoItem)) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setImage(bingoItem.getUrl());
                messageSender.sendPreBuiltMessage(guild.getChannel(), String.format("Absolutely! Added %s to the bingo board!", itemSearch), builder.build());
            }
            else messageSender.sendMessage(guild, "That item is already on the bingo list!");
        }
    }

    @SubCommand({"add-skill", "as"})
    public void addSkill(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);

        if(guild.getBingoRunning()) {
            messageSender.sendMessage(guild, "You can't add items now! Your bingo event is already running!");
            return;
        }

        String skillName = StringUtils.capitalizeFirst(command.getArgs()[1]);
        Integer xp = Integer.parseInt(command.getArgs()[2]);

        BingoSkill skill = skillRepository.findByName(skillName);
        if(skill == null) {
            messageSender.sendMessage(guild, "Sorry, I couldn't find a skill matching the name `%s`.", skillName);
            return;
        }

        BingoSkillTarget target = buildSkillTarget(skill, xp, guildId);

        if(guildService.addSkill(guild, target)) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setImage("https://oldschool.runescape.wiki/images/" + skill.getImageUrl());
            messageSender.sendPreBuiltMessage(guild.getChannel(), String.format("Absolutely! Added %s %s XP to the bingo board!", xp, skill.getName()), builder.build());
        }
        else messageSender.sendMessage(guild, "A goal for that skill is already on the bingo list!");
    }

    /**
     * List all the items currently in a bingo item list
     * @param guildId the guild id
     */
    @SubCommand({"list-items", "li"})
    public void listItems(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);
        if(guild.getItems() != null && !guild.getItems().isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            for (BingoItem item : guild.getItems()) {
                joiner.add(item.getName());
            }
            messageSender.sendMessage(guild, "There is currently (%s) items in the bingo: \\n`[%s]`", guild.getItems().size(), joiner.toString());
        }
        else messageSender.sendMessage(guild, "Yikes! There's no items in the bingo at the moment!");
    }

    @SubCommand({"list-skills", "ls"})
    public void listSkills(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);
        if(guild.getSkills() != null && !guild.getSkills().isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            for (BingoSkillTarget target : guild.getSkills()) {
                joiner.add(target.getSkill().getName());
            }
            messageSender.sendMessage(guild, "There is currently (%s) skills in the bingo: \\n`[%s]`", guild.getSkills().size(), joiner.toString());
        }
        else messageSender.sendMessage(guild, "Yikes! There's no skills in the bingo at the moment!");
    }

    @SubCommand({"list-all", "la"})
    public void listAll(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);
        List<String> items = new ArrayList<>();
        if(guild.getItems() != null) {
            for(BingoItem item : guild.getItems()) {
                items.add(item.getName());
            }
        }

        if(guild.getSkills() != null) {
            for(BingoSkillTarget target : guild.getSkills()) {
                String targetExample = StringUtils.convertShorthand(target.getXpTarget()) + " " + target.getSkill().getName() + " XP";
                items.add(targetExample);
            }
        }

        if(items.isEmpty()) {
            messageSender.sendMessage(guild, "There's no items or skills in the bingo event right now!");
            return;
        }

        StringJoiner joiner = new StringJoiner(", ");
        for(String item : items) {
            joiner.add(item);
        }
        messageSender.sendMessage(guild, "There is currently (%s) tiles in the bingo: \\n`[%s]`", items.size(), joiner.toString());
    }

    /**
     * List all the users currently registered for the bingo event
     */
    @SubCommand({"list-users", "lu"})
    public void listUsers(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);
        if(guild.getUsers() != null && !guild.getUsers().isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            for (BingoUser user : guild.getUsers()) {
                joiner.add(user.getRsName());
            }
            messageSender.sendMessage(guild, "There is currently (%s) users registered for the bingo: \\n`[%s]`", guild.getUsers().size(), joiner.toString());
        }
        else messageSender.sendMessage(guild, "Uh-oh! There's no players registered for the bingo at the moment!");
    }

    @SubCommand({"register", "r"})
    public void registerUser(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);
        String discordId = command.getMember().getId();
        String[] args = command.getArgs();
        if(args.length == 1) {
            registerUserByDiscord(guild, discordId);
        } else {
            String username = StringUtils.stringArrayToString(args, 1);
            registerUserByRsName(guild, username, discordId);
        }
    }

    @SubCommand({"leave", "dr"})
    public void deregisterUser(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);
        BingoUser user = userRepository.findByDiscordId(command.getMember().getId());

        if(user == null) {
            messageSender.sendMessage(guild, "You're not registered for the next bingo event!");
            return;
        }

        guildService.deregisterUser(guild, user);
    }

    @SubCommand({"reset", "rs"})
    public void teardown(String guildId, Command command) {
        BingoGuild guild = getGuildById(guildId);
        guildService.teardown(guild);
        messageSender.sendMessage(guild, "Bingo reset complete. Everything has been cleared!");
    }

    private void registerUserByDiscord(BingoGuild guild, String discordId) {
        BingoUser user = userRepository.findByDiscordId(discordId);
        if(user == null) {
            messageSender.sendMessage(guild, "You haven't ever registered a runescape account before.\n" +
                    "Please register with your in-game RuneScape username at least once.");
            return;
        }
        guildService.registerUser(guild, user);
    }

    private void registerUserByRsName(BingoGuild guild, String username, String discordId) {
        BingoUser user = userRepository.findByRsName(username);

        if(user == null) {
            OsrsClient client = new OsrsClient();
            if(!client.userExists(username)) {
                messageSender.sendMessage(guild, "Sorry, I couldn't find that user! If the hiscores are down try the forceregister command!");
                return;
            }
            user = new BingoUser();
            user.setRsName(username);
            user.setDiscordId(discordId);
            user.setBingosCompleted(0);
            userRepository.save(user);
        }
        guildService.registerUser(guild, user);
    }

    private BingoSkillTarget buildSkillTarget(BingoSkill skill, Integer xp, String guildId) {
        BingoSkillTarget target = new BingoSkillTarget();
        target.setSkill(skill);
        target.setXpTarget(xp);
        target.setBingoGuildId(guildId);
        skillTargetRepository.save(target);
        return target;
    }

    /**
     * The best part about this method is that over time this will increasingly
     * become faster and faster as we have more items enter the database.
     * @param name the name of the item
     * @return the bingo item object
     */
    private BingoItem loadOrCreateBingoItem(String name) {
        BingoItem bingoItem = itemRepository.findByName(name);
        if(bingoItem == null) {
            OsrsBoxClient client = new OsrsBoxClient();
            OsrsBoxResponse response = client.lookup(name);

            if(response == null || response.getItems().isEmpty()) {
                return null;
            }

            OsrsItem osrsItem = response.getItems().get(0);
            String image = client.loadItemImage(osrsItem);
            bingoItem = new BingoItem();
            bingoItem.setName(name);
            bingoItem.setUrl(image);
            itemRepository.save(bingoItem);
        }
        return bingoItem;
    }


    /**
     * Gets a guild by its discord guild id
     * @param id the id of the guild
     * @return a bingo guild object
     */
    public BingoGuild getGuildById(String id) {
        return guildService.getGuildById(id);
    }

}