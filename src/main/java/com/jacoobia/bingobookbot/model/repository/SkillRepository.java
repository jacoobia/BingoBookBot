package com.jacoobia.bingobookbot.model.repository;

import com.jacoobia.bingobookbot.model.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Integer> {

    Optional<Skill> findByName(String name);

}