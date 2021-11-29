package com.jacoobia.bingobookbot.model.repository;

import com.jacoobia.bingobookbot.model.guild.BingoGuild;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BingoGuildRepository extends JpaRepository<BingoGuild, Integer> {

    BingoGuild getBingoGuildByGuildId(String guildId);

}