package com.jacoobia.bingobookbot.model.guild;

import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import com.jacoobia.bingobookbot.model.entities.Item;
import com.jacoobia.bingobookbot.model.entities.SkillTarget;
import com.jacoobia.bingobookbot.model.entities.User;
import com.jacoobia.bingobookbot.service.GuildService;
import com.jacoobia.bingobookbot.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.StringJoiner;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GuildHelper {

    private final GuildService guildService;

    public String listItems(BingoGuild guild) {
        List<Item> items = CollectionUtils.getListNullSafe(guild.getItems());
        StringJoiner joiner = new StringJoiner(", ");
        items.stream().map(Item::getName).forEach(joiner::add);
        return joiner.toString();
    }

    public String listSkills(BingoGuild guild) {
        List<SkillTarget> targets = guild.getSkills();
        StringJoiner joiner = new StringJoiner(", ");
        targets.stream().map(target -> target.getSkill().getName()).forEach(joiner::add);
        return joiner.toString();
    }

    public String listUsers(BingoGuild guild) {
        List<User> users = CollectionUtils.getListNullSafe(guild.getUsers());
        StringJoiner joiner = new StringJoiner(", ");
        users.stream().map(User::getRsName).forEach(joiner::add);
        return joiner.toString();
    }
}
