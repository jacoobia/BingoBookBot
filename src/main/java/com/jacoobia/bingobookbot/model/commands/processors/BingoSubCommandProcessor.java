package com.jacoobia.bingobookbot.model.commands.processors;

import com.jacoobia.bingobookbot.annotations.SubCommand;
import com.jacoobia.bingobookbot.api.osrs.OsrsClient;
import com.jacoobia.bingobookbot.api.osrsbox.OsrsBoxClient;
import com.jacoobia.bingobookbot.model.commands.Command;
import com.jacoobia.bingobookbot.model.entities.*;
import com.jacoobia.bingobookbot.model.entities.osrs.OsrsBoxResponse;
import com.jacoobia.bingobookbot.model.entities.osrs.OsrsItem;
import com.jacoobia.bingobookbot.model.guild.GuildHelper;
import com.jacoobia.bingobookbot.service.GuildService;
import com.jacoobia.bingobookbot.service.ItemService;
import com.jacoobia.bingobookbot.service.SkillService;
import com.jacoobia.bingobookbot.service.UserService;
import com.jacoobia.bingobookbot.utils.BoardPainter;
import com.jacoobia.bingobookbot.utils.CollectionUtils;
import com.jacoobia.bingobookbot.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The service layer for a Bingo, this contains all the helpful service layer
 * methods as well as all the {@link SubCommand}'s since there won't be many
 * service layer methods exposed to the controllers as everything is done
 * via the discord bot rather than the RuneLite plugin.
 *
 * @author Jacob
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BingoSubCommandProcessor {

    /* Other Service Layers */
    private final ItemService itemService;
    private final UserService userService;
    private final SkillService skillService;
    private final GuildService guildService;

    /* Helpers */
    private final GuildHelper guildHelper;

    /**
     * Register the bingo bot channel for a guild, this will be the text
     * channel that the Bingo Bot will send all the messages to, further
     * down the line in the {@link GuildService} it also ensures that the
     * server has been registered with the bot.
     */
    @SubCommand({"channel", "c"})
    public void registerChannel(Command command) {
        guildService.setChannel(command.getGuild(), command.getChannel());
    }

    /**
     * Start the bingo event for a guild, this will set the {@link BingoGuild}'s
     * bingoRunning field to true which will prevent most actions from being
     * performed in the corresponding discord server. Then a new instance of
     * the {@link BoardPainter} class is created, and we add all the skills and
     * items to it to paint the board and send it to the discord channel.
     */
    @SubCommand({"start", "s"})
    public void startBingo(Command command) {
        BingoGuild guild = command.getBingoGuild();

        //Set the bingo as started in the guild object
        guildService.startBingo(guild);

        //Grab the items and skills
        List<Item> items = CollectionUtils.getListNullSafe(guild.getItems());
        List<SkillTarget> skills = CollectionUtils.getListNullSafe(guild.getSkills());

        //Create a new board painter instance
        BoardPainter painter = new BoardPainter();

        //Add each of the items and skills to the painter
        items.forEach(painter::add);
        skills.forEach(painter::add);

        //Paint the board and encode it to an array of bytes
        byte[] board = painter.paintAndEncode();

        //Finally, send the image data to the discord server
        guild.sendImage(guild.getBingoName(), board);
    }

    /**
     * Set the name of a bingo guild and then announce it into the chat
     * for some visual feedback for the user who tried to change the name
     * of the bingo.
     */
    @SubCommand({"name", "n"})
    public void setName(Command command) {
        String guildId = command.getGuild().getId();
        String name = StringUtils.stringArrayToString(command.getArgs(), 1);
        guildService.setBingoName(guildId, name);
    }

    /**
     * Stop the bingo event for a guild if it's running, otherwise alert
     * the users that there isn't an event running. This will set the
     * bingoRunning bool to false for the guild, wipe the users, clear the
     * bingo name, clear all items and skills added and then print the
     * results of the bingo.
     * todo: set up quartz scheduler to loop through all bingos,and allow for scheduling of bingos
     */
    @SubCommand({"stop", "s"})
    public void stopBingo(Command command) {
        BingoGuild guild = command.getBingoGuild();
        if(guild.getBingoRunning()) {
            guild.sendMessage("The bingo event %s has now ended!", guild.getBingoName());
            guildService.stopBingo(guild.getGuildId());
            //todo print bingo results
        }
        else guild.sendMessage("There's no bingo event running at the moment!");
    }

    /**
     * Takes in args from the add command, parses them into item names
     * then looks up and adds items to a guild's bingo item list. Items
     * are first looked up in the project database, then if they don't
     * have an entry an API call to the osrsbox restful API for runescape
     * items is processed and the output is used to create a {@link Item}
     * to insert into the database for quicker look-ups. Also ensures that
     * the input item isn't already in the bingo so there's no duplicates.
     */
    @SubCommand({"add-item", "ai"})
    public void addItem(Command command) {
        BingoGuild guild = command.getBingoGuild();
        String guildId = command.getGuild().getId();

        if(guild.getBingoRunning() ) {
            guild.sendMessage("You can't add items now! Your bingo event is already running!");
            return;
        }

        String parsed = StringUtils.stringArrayToString(command.getArgs(), 1);
        String[] items = parsed.split(", ");

        for(String item : items) {
            String itemSearch = StringUtils.capitalizeFirst(item);
            Item bingoItem = loadOrCreateBingoItem(itemSearch);

            if(bingoItem == null) {
                guild.sendMessage("Sorry, I couldn't find the item: %s", itemSearch);
                return;
            }

            if(guildService.addItem(guildId, bingoItem)) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setImage(bingoItem.getUrl());
                guild.sendPreBuiltMessage(String.format("Absolutely! Added %s to the bingo board!", itemSearch), builder.build());
            }
            else guild.sendMessage("That item is already on the bingo list!");
        }
    }

    /**
     * Takes in the command and parses the args to read the name of the
     * skill and the amount of experience as the target. This will look
     * up the skill from the database to validate the input and will
     * store that info into a {@link SkillTarget} object to be
     * saved into the database.
     */
    @SubCommand({"add-skill", "as"})
    public void addSkill(Command command) {
        BingoGuild guild = command.getBingoGuild();
        if(guild.getBingoRunning()) {
            guild.sendMessage("You can't add items now! Your bingo event is already running!");
            return;
        }

        String skillName = StringUtils.capitalizeFirst(command.getArgs()[1]);
        Integer xp = Integer.parseInt(command.getArgs()[2]);
        Skill skill = skillService.findSkill(skillName);
        if(skill == null) {
            guild.sendMessage("Sorry, I couldn't find a skill matching the name `%s`.", skillName);
            return;
        }

        SkillTarget target = skillService.createSkillTarget(skill, xp, guild);
        if(guildService.addSkill(guild, target)) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setImage("https://oldschool.runescape.wiki/images/" + skill.getImageUrl());
            guild.sendPreBuiltMessage(String.format("Absolutely! Added %s %s XP to the bingo board!", xp, skill.getName()), builder.build());
        }
        else guild.sendMessage("A goal for that skill is already on the bingo list!");
    }

    /**
     * List all the items currently in a bingo and formats them
     * into a discord code block list for the users.
     */
    @SubCommand({"list-items", "li"})
    public void listItems(Command command) {
        BingoGuild guild = command.getBingoGuild();
        if(CollectionUtils.isListNotEmpty(guild.getItems())) {
            String list = guildHelper.listItems(guild);
            guild.sendMessage("There is currently (%s) items in the bingo: \\n`[%s]`", guild.getItems().size(), list);
        }
        else guild.sendMessage("Yikes! There's no items in the bingo at the moment!");
    }

    /**
     * List all the skills currently in a bingo and formats them
     * into a discord code block list for the users.
     */
    @SubCommand({"list-skills", "ls"})
    public void listSkills(Command command) {
        BingoGuild guild = command.getBingoGuild();
        if(CollectionUtils.isListNotEmpty(guild.getSkills())) {
            String list = guildHelper.listSkills(guild);
            guild.sendMessage("There is currently (%s) skills in the bingo: \\n`[%s]`", guild.getSkills().size(), list);
        }
        else guild.sendMessage("Yikes! There's no skills in the bingo at the moment!");
    }

    /**
     * Gets all the items and skills in the bingo event currently and
     * counts and formats them into a discord code block list to show
     * to the users.
     */
    @SubCommand({"list-all", "la"})
    public void listAll(Command command) {
        BingoGuild guild = command.getBingoGuild();
        List<String> items = new ArrayList<>();
        if(CollectionUtils.isListEmpty(guild.getItems())) {
            items = guild.getItems().stream().map(Item::getName).collect(Collectors.toList());
        }

        if(CollectionUtils.isListEmpty(guild.getSkills())) {
            List<SkillTarget> skillTargets = guild.getSkills();
            for(SkillTarget target : skillTargets) {
                String targetExample = StringUtils.convertShorthand(target.getXpTarget()) + " " + target.getSkill().getName() + " XP";
                items.add(targetExample);
            }
        }

        if(items.isEmpty()) {
            guild.sendMessage("There's no items or skills in the bingo event right now!");
            return;
        }

        String list = CollectionUtils.joinStringList(items);
        guild.sendMessage("There is currently (%s) tiles in the bingo: \\n`[%s]`", items.size(), list);
    }

    /**
     * Clears all the items currently in the {@link Item} list for
     * a guild's bingo event. This clears it by just setting the
     * list to {@link Collections#emptyList()}.
     */
    @SubCommand({"clear-items", "ci"})
    public void clearItems(Command command) {

    }

    /**
     * List all the users currently registered for the bingo event
     * by looking them up, combining them with a string joiner and
     * then printing the result in a discord code block for formatting.
     */
    @SubCommand({"list-users", "lu"})
    public void listUsers(Command command) {
        BingoGuild guild = command.getBingoGuild();
        if(CollectionUtils.isListEmpty(guild.getUsers())) {
            String list = guildHelper.listUsers(guild);
            guild.sendMessage("There is currently (%s) users registered for the bingo: \\n`[%s]`", guild.getUsers().size(), list);
        }
        else guild.sendMessage("Uh-oh! There's no players registered for the bingo at the moment!");
    }

    /**
     * Registers a user for a bingo event in a server, users are not
     * limited on how many bingos they can register for at one time
     * because you can only have 1 bingo per server anyway. This can
     * accept an OSRS username or just take the register command and
     * look up their username from our database records.
     */
    @SubCommand({"register", "r"})
    public void registerUser(Command command) {
        BingoGuild guild = command.getBingoGuild();
        String discordId = command.getMember().getId();
        String[] args = command.getArgs();
        if(args.length == 1) {
            registerUserByDiscord(guild, discordId);
        } else {
            String username = StringUtils.stringArrayToString(args, 1);
            registerUserByRsName(guild, username, discordId);
        }
    }

    /**
     * Deregisters a user from a bingo event, this does validate
     * that the user is registered and is in our database before
     * we actually do anything otherwise we could get some issues.
     */
    @SubCommand({"leave", "dr"})
    public void deregisterUser(Command command) {
        BingoGuild guild = command.getBingoGuild();
        User user = userService.find(command.getMember().getId());

        if(user == null) {
            guild.sendMessage("You're not registered for the next bingo event!");
            return;
        }

        guildService.deregisterUser(guild, user);
    }

    /**
     * Resets the bingo entirely, clearing all the data from the
     * bingo guild and setting it to not be active and then saving it.
     */
    @SubCommand({"reset", "rs"})
    public void teardown(Command command) {
        BingoGuild guild = command.getBingoGuild();
        guildService.teardown(guild);
        guild.sendMessage("Bingo reset complete. Everything has been cleared!");
    }

    /**
     * Register a user to the bingo with their discord ID, this
     * requires the user to have registered with their osrs username
     * at least once before because it will look up their osrs username
     * from our users table. Doesn't require username validation because
     * this assumed that the user has been validated before.
     * WARNING : Users can use !bingo forceregister if given the permission tom
     *            I need to come up with an elegant solution to automatically enable
     *            the force register ONLY when the hiscores are down
     * @param guild the guild to register the user to
     * @param discordId the discord id for the user
     */
    private void registerUserByDiscord(BingoGuild guild, String discordId) {
        User user = userService.find(discordId);
        if(user == null) {
            guild.sendMessage("You haven't ever registered a runescape account before.\n" +
                    "Please register with your in-game RuneScape username at least once.");
            return;
        }
        guildService.registerUser(guild, user);
    }

    /**
     * Registers a user for a bingo event with their given osrs username,
     * requires a valid osrs username since it will do a hiscores lookup
     * to validate that the user exists (this also incidentally makes it
     * so that new account cannot register for an event which would avoid
     * issues with bots etc.)
     * @param guild the guild to register the user to
     * @param username the username
     * @param discordId the users discord id
     */
    private void registerUserByRsName(BingoGuild guild, String username, String discordId) {
        User user = userService.findUser(username);
        if(user == null) {
            OsrsClient client = new OsrsClient();
            if(!client.userExists(username)) {
                guild.sendMessage("Sorry, I couldn't find that user! If the hiscores are down try the forceregister command!");
                return;
            }
            user = userService.createUser(username, discordId);
        }
        guildService.registerUser(guild, user);
    }

    /**
     * The best part about this method is that over time this will increasingly
     * become faster and faster as we have more items enter the database.
     * @param name the name of the item
     * @return the bingo item object
     */
    private Item loadOrCreateBingoItem(String name) {
        Item item = itemService.find(name);
        if(item == null) {
            OsrsBoxClient client = new OsrsBoxClient();
            OsrsBoxResponse response = client.lookup(name);

            if(response == null || response.getItems().isEmpty()) {
                return null;
            }

            OsrsItem osrsItem = response.getItems().get(0);
            item = itemService.createItem(osrsItem);
        }
        return item;
    }

}