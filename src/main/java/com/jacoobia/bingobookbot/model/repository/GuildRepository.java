package com.jacoobia.bingobookbot.model.repository;

import com.jacoobia.bingobookbot.model.entities.BingoGuild;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuildRepository extends JpaRepository<BingoGuild, Integer> {

    BingoGuild getBingoGuildByGuildId(String guildId);

}