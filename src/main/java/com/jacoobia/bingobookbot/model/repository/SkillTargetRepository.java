package com.jacoobia.bingobookbot.model.repository;

import com.jacoobia.bingobookbot.model.entities.SkillTarget;
import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillTargetRepository extends JpaRepository<SkillTarget, Integer> {

    List<SkillTarget> findAllByGuild(BingoGuild guild);

}