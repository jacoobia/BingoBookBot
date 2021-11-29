package com.jacoobia.bingobookbot.model.repository;

import com.jacoobia.bingobookbot.model.entities.BingoSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BingoSkillRepository extends JpaRepository<BingoSkill, Integer> {

    BingoSkill findByName(String name);

}