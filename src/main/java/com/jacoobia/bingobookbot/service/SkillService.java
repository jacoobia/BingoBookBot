package com.jacoobia.bingobookbot.service;

import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import com.jacoobia.bingobookbot.model.entities.Skill;
import com.jacoobia.bingobookbot.model.entities.SkillTarget;
import com.jacoobia.bingobookbot.model.repository.SkillRepository;
import com.jacoobia.bingobookbot.model.repository.SkillTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillTargetRepository skillTargetRepository;

    private final GuildService guildService;

    /**
     * Find a skill target by its id
     * @param id the id of the target
     * @return a {@link SkillTarget} object
     */
    public SkillTarget findTarget(Integer id) {
        Optional<SkillTarget> found = skillTargetRepository.findById(id);
        return found.orElse(null);
    }

    /**
     * Get all the {@link SkillTarget} objects for a guild
     * @param guild the guild
     * @return a list of {@link SkillTarget}
     */
    public List<SkillTarget> getTargetsForGuild(BingoGuild guild) {
        return skillTargetRepository.findAllByGuild(guild);
    }

    /**
     * Find a skill by its id
     * @param id the id of the skill
     * @return a {@link Skill} object or null
     */
    public Skill findSkill(Integer id) {
        Optional<Skill> found = skillRepository.findById(id);
        return found.orElse(null);
    }

    /**
     * Find a skill by its name
     * @param name the name of the skill
     * @return a {@link Skill} object or null
     */
    public Skill findSkill(String name) {
        Optional<Skill> found = skillRepository.findByName(name);
        return found.orElse(null);
    }

    /**
     * Drops all the skill targets from the database for a given {@link BingoGuild}
     * @param guild the guild
     */
    public void clearSkillTargetsForGuild(BingoGuild guild) {
        List<SkillTarget> targets = getTargetsForGuild(guild);
        skillTargetRepository.deleteAll(targets);
    }

    /**
     * Create a new skill target for a {@link BingoGuild} and
     * save it into the database.
     * @param skill the skill
     * @param xp the xp target
     * @param guild the guild
     * @return a {@link SkillTarget} object
     */
    public SkillTarget createSkillTarget(Skill skill, Integer xp, BingoGuild guild) {
        SkillTarget target = new SkillTarget();
        target.setSkill(skill);
        target.setXpTarget(xp);
        target.setGuild(guild);
        skillTargetRepository.save(target);
        return target;
    }
}