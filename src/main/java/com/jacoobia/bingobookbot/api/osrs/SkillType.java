package com.jacoobia.bingobookbot.api.osrs;

import com.jacoobia.bingobookbot.model.entities.Skill;
import com.jacoobia.bingobookbot.model.repository.SkillRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Archive class, this is only for if we have some kind of issue somewhere down the line.
 * Please use {@link Skill} and the {@link SkillRepository} instead.
 */
@Getter
@AllArgsConstructor
public enum SkillType {
    OVERALL("Overall", "Skills_icon.png"),
    ATTACK("Attack", "Attack_icon.png"),
    DEFENCE("Defence", "Defence_icon.png"),
    STRENGTH("Strength", "Strength_icon.png"),
    HITPOINTS("Hitpoints", "Hitpoints_icon.png"),
    RANGED("Ranged", "Ranged_icon.png"),
    PRAYER("Prayer", "Prayer_icon.png"),
    MAGIC("Magic", "Magic_icon.png"),
    COOKING("Cooking", "Cooking_icon.png"),
    WOODCUTTING("Woodcutting", "Woodcutting_icon.png"),
    FLETCHING("Fletching", "Fletching_icon.png"),
    FISHING("Fishing", "Fishing_icon.png"),
    FIREMAKING("Firemaking", "Firemaking_icon.png"),
    CRAFTING("Crafting", "Crafting_icon.png"),
    SMITHING("Smithing", "Smithing_icon.png"),
    MINING("Mining", "Mining_icon.png"),
    HERBLORE("Herblore", "Herblore_icon.png"),
    AGILITY("Agility", "Agility_icon.png"),
    THIEVING("Thieving", "Thieving_icon.png"),
    SLAYER("Slayer", "Slayer_icon.png"),
    FARMING("Farming", "Farming_icon.png"),
    RUNECRAFT("Runecraft", "Runecraft_icon.png"),
    HUNTER("Hunter", "Hunter_icon.png"),
    CONSTRUCTION("Construction", "Construction_icon.png")
    ;
    
    private final String name;
    private final String imageUrl;

    public static SkillType getMatch(String name) {
        return Arrays.stream(values())
                .filter(skill -> skill.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

}
